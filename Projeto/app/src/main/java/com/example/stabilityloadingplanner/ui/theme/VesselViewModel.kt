package com.example.stabilityloadingplanner.ui.theme

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stabilityloadingplanner.api.GeminiContent
import com.example.stabilityloadingplanner.api.GeminiConfig
import com.example.stabilityloadingplanner.api.GeminiPart
import com.example.stabilityloadingplanner.api.GeminiRequest
import com.example.stabilityloadingplanner.api.RetrofitClient
import com.example.stabilityloadingplanner.data.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

data class SearchHistoryItem(val imo: String, val name: String)

class VesselViewModel : ViewModel() {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var activeVessel             by mutableStateOf(Vessel())
        private set
    var activeVesselHasEstimates by mutableStateOf(false)
        private set
    var searchResult             by mutableStateOf<Vessel?>(null)
        private set
    var currentVoyage            by mutableStateOf(Voyage())
        private set
    var latitude                 by mutableStateOf(38.69)
    var longitude                by mutableStateOf(-9.31)
    var marineData               by mutableStateOf<HourlyMarineData?>(null)
        private set
    var isLoadingMarine          by mutableStateOf(false)
        private set
    var marineError              by mutableStateOf<String?>(null)
        private set
    var vesselPhotoUrl           by mutableStateOf<String?>(null)
        private set
    var isLoadingPhoto           by mutableStateOf(false)
        private set
    var isSearchingVessel        by mutableStateOf(false)
        private set
    var vesselSearchError        by mutableStateOf<String?>(null)
        private set
    var searchHasEstimates       by mutableStateOf(false)
        private set
    var snackbarMessage          by mutableStateOf<String?>(null)
        private set
    var searchHistory            by mutableStateOf<List<SearchHistoryItem>>(emptyList())
        private set
    var isUsingAI                by mutableStateOf(false)
        private set

    fun clearSnackbar() { snackbarMessage = null }

    val waveSafetyLimit         = 3.0
    val isSafetyRisk: Boolean        get() = (marineData?.wave_height?.firstOrNull() ?: 0.0) > waveSafetyLimit
    val hasVesselSelected: Boolean       get() = activeVessel.name.isNotEmpty()
    val searchResultIsComplete: Boolean  get() = searchResult?.name?.isNotEmpty() == true

    val tanks = mutableStateListOf<Tank>()

    init {
        FirebaseAuth.getInstance().addAuthStateListener { fa ->
            val user = fa.currentUser
            if (user != null) {
                loadSearchHistory()
            } else {
                searchHistory = emptyList(); searchResult = null
                vesselPhotoUrl = null; vesselSearchError = null
                activeVessel = Vessel(); activeVesselHasEstimates = false
                tanks.clear()
            }
        }
    }

    // ── Seleccionar navio ─────────────────────────────────────────────────────

    fun selectVessel(vessel: Vessel) {
        activeVessel             = vessel
        activeVesselHasEstimates = searchHasEstimates
        VesselDatabase.addVessel(vessel)
        tanks.clear()
        buildTanks(vessel.deadweight, vessel.numberOfHolds)
        fetchVesselPhoto(vessel.name, vessel.imo)
    }

    fun updateActiveVesselDimensions(
        deadweight: Double,
        numberOfHolds: Int,
        loa: Double?  = null,
        beam: Double? = null
    ) {
        val effectiveLoa = when {
            loa  != null && loa  > 0 -> loa
            activeVessel.loa  > 0    -> activeVessel.loa
            else -> estimateLoa(activeVessel.name, deadweight)
        }
        val effectiveBeam = when {
            beam != null && beam > 0 -> beam
            activeVessel.beam > 0    -> activeVessel.beam
            else -> estimateBeam(activeVessel.name, effectiveLoa)
        }
        val depth = effectiveBeam / 1.65
        val updated = activeVessel.copy(
            deadweight      = deadweight,
            numberOfHolds   = numberOfHolds,
            loa             = effectiveLoa,
            beam            = effectiveBeam,
            lightshipWeight = deadweight * 0.22,
            lightshipKG     = depth * 0.55
        )
        activeVessel             = updated
        activeVesselHasEstimates = false
        VesselDatabase.addVessel(updated)
        viewModelScope.launch { saveVesselToFirestore(updated, isEstimated = false) }
        tanks.clear()
        buildTanks(deadweight, numberOfHolds)
    }

    private fun buildTanks(deadweight: Double, numberOfHolds: Int) {
        val forePeakCap = (deadweight * 0.05).toInt()
        val aftPeakCap  = (deadweight * 0.04).toInt()
        val remaining   = deadweight - forePeakCap - aftPeakCap
        val validHolds  = if (numberOfHolds > 0) numberOfHolds else 1
        val holdCap     = (remaining / validHolds).toInt()
        tanks.add(Tank(name = "Fore Peak", capacity = "${forePeakCap}t"))
        for (i in 1..validHolds) tanks.add(Tank(name = "Hold $i", capacity = "${holdCap}t"))
        tanks.add(Tank(name = "Aft Peak", capacity = "${aftPeakCap}t"))
    }

    // ── Pesquisa: Firestore → VesselAPI → Gemini AI → estimativas ────────────

    fun searchVesselByImo(imo: String) {
        vesselSearchError  = null
        searchHasEstimates = false
        isUsingAI          = false
        viewModelScope.launch {
            isSearchingVessel = true
            try {
                // 1. Firestore
                val (firestoreVessel, firestoreIsEstimated) = getVesselFromFirestore(imo.trim())
                if (firestoreVessel != null && firestoreVessel.deadweight > 0 && firestoreVessel.loa > 0) {
                    searchResult       = firestoreVessel
                    searchHasEstimates = firestoreIsEstimated
                    fetchVesselPhoto(firestoreVessel.name, firestoreVessel.imo)
                    saveSearchHistory(imo.trim(), firestoreVessel.name)
                    Log.d("VESSEL", "Firestore: ${firestoreVessel.name}")
                    return@launch
                }

                // 2. VesselAPI
                var vessel: Vessel? = null
                var estimated       = true
                try {
                    val response = RetrofitClient.vesselApi.getVesselByImo(
                        auth = "Bearer ${RetrofitClient.VESSEL_API_KEY}",
                        id   = imo.trim()
                    )
                    val data = response.vessel
                    if (data != null && !data.name.isNullOrBlank()) {
                        vessel    = buildVesselFromApiData(imo, data)
                        estimated = isEstimated(data)
                        Log.d("VESSEL_API", "API: ${data.name} dwt=${data.dimensions?.deadweight} estimated=$estimated")
                    }
                } catch (e: Exception) {
                    Log.e("VESSEL_API", "Erro VesselAPI: ${e.message}")
                }

                // 3. Gemini AI — se não há dados reais
                if (vessel == null || estimated) {
                    val aiVessel = lookupVesselWithAI(imo.trim(), vessel?.name ?: "")
                    if (aiVessel != null) {
                        vessel    = aiVessel
                        estimated = aiVessel.deadweight <= 0 || aiVessel.loa <= 0
                    }
                }

                if (vessel != null) {
                    searchResult       = vessel
                    searchHasEstimates = estimated
                    saveVesselToFirestore(vessel, estimated)
                    saveSearchHistory(imo.trim(), vessel.name)
                    fetchVesselPhoto(vessel.name, vessel.imo)
                } else {
                    fallbackToLocalDb(imo)
                }
            } catch (e: Exception) {
                Log.e("VESSEL", "Erro geral: ${e.message}")
                fallbackToLocalDb(imo)
            } finally {
                isSearchingVessel = false
                isUsingAI         = false
            }
        }
    }

    // ── Gemini AI lookup ──────────────────────────────────────────────────────

    private suspend fun lookupVesselWithAI(imo: String, knownName: String): Vessel? {
        val apiKey = RetrofitClient.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            Log.w("GEMINI", "⚠ GEMINI_API_KEY não configurada — a saltar IA")
            return null
        }

        return try {
            isUsingAI = true
            val nameHint = if (knownName.isNotBlank()) ", vessel name: $knownName" else ""

            val prompt = """You are a maritime vessel database.
For IMO number $imo$nameHint, provide the vessel specifications.
Reply with ONLY a raw JSON object, no markdown, no code blocks, no explanation:
{"name":"","type":"","deadweight":0,"loa":0.0,"beam":0.0,"numberOfHolds":0,"grossTonnage":0.0}
Rules:
- type must be: Bulk Carrier, Container Ship, Tanker, General Cargo Ship, Ferry, RoRo, or Passenger Ship
- use 0 for unknown values
- if you don't recognise this IMO, return all zeros"""

            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(prompt)))),
                generationConfig = GeminiConfig(temperature = 0.1, maxOutputTokens = 300)
            )

            val response = RetrofitClient.geminiApi.generateContent(apiKey, request)
            val rawText  = response.candidates
                ?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: run { Log.e("GEMINI", "Resposta vazia"); return null }

            Log.d("GEMINI", "Resposta: $rawText")
            val json = extractJsonFromText(rawText)
            val obj  = JSONObject(json)

            val dwt   = obj.optDouble("deadweight",  0.0)
            val loa   = obj.optDouble("loa",         0.0)
            val beam  = obj.optDouble("beam",        0.0)
            val gt    = obj.optDouble("grossTonnage",0.0)
            val holds = obj.optInt("numberOfHolds",  0)
            val name  = obj.optString("name",        "").ifBlank { knownName }
            val type  = obj.optString("type",        "General Cargo Ship")

            if (dwt <= 0 && loa <= 0 && gt <= 0) {
                Log.d("GEMINI", "IA não reconheceu IMO $imo")
                return null
            }

            Log.d("GEMINI", "✔ IA: $name dwt=$dwt loa=$loa beam=$beam")

            val effectiveDwt  = if (dwt  > 0) dwt  else if (gt  > 0) estimateDwtFromGT(gt, type)
            else if (loa > 0) estimateDwtFromLoa(loa, type)
            else estimateDwtFromType(type)
            val effectiveLoa  = if (loa  > 0) loa  else estimateLoa(type, effectiveDwt)
            val effectiveBeam = if (beam > 0) beam else estimateBeam(type, effectiveLoa)
            val depth         = effectiveBeam / 1.65

            Vessel(
                imo             = imo,
                name            = name,
                loa             = effectiveLoa,
                beam            = effectiveBeam,
                displacement    = effectiveDwt + effectiveDwt * 0.22,
                lightshipWeight = effectiveDwt * 0.22,
                lightshipKG     = depth * 0.55,
                deadweight      = effectiveDwt,
                numberOfHolds   = if (holds > 0) holds else estimateHolds(type, effectiveDwt)
            )
        } catch (e: Exception) {
            Log.e("GEMINI", "Erro: ${e.javaClass.simpleName} — ${e.message}")
            null
        } finally {
            isUsingAI = false
        }
    }

    private fun extractJsonFromText(text: String): String {
        val cleaned = text.replace("```json", "").replace("```", "").trim()
        val start   = cleaned.indexOf('{')
        val end     = cleaned.lastIndexOf('}')
        return if (start != -1 && end > start) cleaned.substring(start, end + 1) else cleaned
    }

    // ── Construir navio a partir da VesselAPI ─────────────────────────────────

    private fun buildVesselFromApiData(imo: String, data: VesselApiDetail): Vessel {
        val type   = data.type ?: ""
        val gt     = data.dimensions?.grossTonnage
        val apiLoa = data.dimensions?.length?.takeIf { it > 0 }

        val dwt = data.dimensions?.deadweight?.takeIf { it > 0 }
            ?: estimateDwtFromGT(gt, type).takeIf { it > 0 }
            ?: estimateDwtFromLoa(apiLoa ?: 0.0, type).takeIf { it > 0 }
            ?: estimateDwtFromType(type)

        val loa   = apiLoa ?: estimateLoa(type, dwt)
        val beam  = data.dimensions?.beam?.takeIf { it > 0 } ?: estimateBeam(type, loa)
        val depth = beam / 1.65

        return Vessel(
            imo             = data.imo?.toString() ?: imo,
            name            = data.name ?: "",
            loa             = loa,
            beam            = beam,
            displacement    = dwt + dwt * 0.22,
            lightshipWeight = dwt * 0.22,
            lightshipKG     = depth * 0.55,
            deadweight      = dwt,
            numberOfHolds   = estimateHolds(type, dwt)
        )
    }

    private fun isEstimated(data: VesselApiDetail): Boolean =
        (data.dimensions?.deadweight ?: 0.0) <= 0 || (data.dimensions?.length ?: 0.0) <= 0

    // ── Fórmulas de estimativa ────────────────────────────────────────────────

    private fun estimateDwtFromLoa(loa: Double, type: String): Double {
        if (loa <= 0) return 0.0
        val coeff = when {
            type.contains("tanker",    ignoreCase = true) -> 0.50
            type.contains("bulk",      ignoreCase = true) -> 0.40
            type.contains("container", ignoreCase = true) -> 0.25
            else -> 0.30
        }
        return coeff * Math.pow(loa, 2.1)
    }

    private fun estimateLoa(type: String, dwt: Double): Double {
        if (dwt <= 0) return 0.0
        return when {
            type.contains("container", ignoreCase = true) -> 5.0 * Math.pow(dwt, 0.35)
            type.contains("tanker",    ignoreCase = true) -> 4.8 * Math.pow(dwt, 0.35)
            type.contains("bulk",      ignoreCase = true) -> 4.5 * Math.pow(dwt, 0.35)
            else -> 4.2 * Math.pow(dwt, 0.35)
        }
    }

    private fun estimateBeam(type: String, loa: Double): Double {
        if (loa <= 0) return 0.0
        return when {
            type.contains("tanker",    ignoreCase = true) -> (loa / 6.2).coerceIn(8.0, 65.0)
            type.contains("container", ignoreCase = true) -> (loa / 6.8).coerceIn(8.0, 60.0)
            type.contains("bulk",      ignoreCase = true) -> (loa / 7.1).coerceIn(8.0, 65.0)
            else -> (loa / 7.0).coerceIn(6.0, 60.0)
        }
    }

    private fun estimateDwtFromGT(gt: Double?, type: String?): Double {
        if (gt == null || gt <= 0) return 0.0
        return when {
            type?.contains("container", ignoreCase = true) == true -> gt * 1.05
            type?.contains("tanker",    ignoreCase = true) == true -> gt * 1.80
            type?.contains("bulk",      ignoreCase = true) == true -> gt * 1.70
            type?.contains("general",   ignoreCase = true) == true -> gt * 1.35
            type?.contains("cargo",     ignoreCase = true) == true -> gt * 1.35
            else -> gt * 1.30
        }
    }

    private fun estimateDwtFromType(type: String): Double = when {
        type.contains("container", ignoreCase = true) -> 60000.0
        type.contains("bulk",      ignoreCase = true) -> 75000.0
        type.contains("tanker",    ignoreCase = true) -> 100000.0
        type.contains("general",   ignoreCase = true) -> 15000.0
        type.contains("cargo",     ignoreCase = true) -> 20000.0
        type.contains("ferry",     ignoreCase = true) ->  8000.0
        type.contains("roro",      ignoreCase = true) -> 15000.0
        else -> 30000.0
    }

    private fun estimateHolds(type: String, dwt: Double): Int {
        if (dwt <= 0) return 2
        return when {
            type.contains("bulk", ignoreCase = true) -> when {
                dwt < 35000  -> 4; dwt < 60000 -> 5
                dwt < 80000  -> 7; dwt < 120000 -> 8; else -> 9
            }
            type.contains("container", ignoreCase = true) -> (dwt / 12000).toInt().coerceIn(2, 16)
            type.contains("tanker",    ignoreCase = true) -> (dwt / 15000).toInt().coerceIn(6, 14)
            else -> (dwt / 8000).toInt().coerceIn(1, 8)
        }
    }

    private fun fallbackToLocalDb(imo: String) {
        searchResult = VesselDatabase.getByImo(imo.trim())
        if (searchResult == null) vesselSearchError = "Vessel not found. Check the IMO or register manually."
        else fetchVesselPhoto(searchResult!!.name, searchResult!!.imo)
    }

    // ── Firestore ─────────────────────────────────────────────────────────────

    private suspend fun getVesselFromFirestore(imo: String): Pair<Vessel?, Boolean> {
        return try {
            val doc = db.collection("vessels").document(imo).get().await()
            if (!doc.exists()) return Pair(null, true)
            val vessel = Vessel(
                imo             = doc.getString("imo")             ?: imo,
                name            = doc.getString("name")            ?: return Pair(null, true),
                loa             = doc.getDouble("loa")             ?: 0.0,
                beam            = doc.getDouble("beam")            ?: 0.0,
                deadweight      = doc.getDouble("deadweight")      ?: 0.0,
                lightshipWeight = doc.getDouble("lightshipWeight") ?: 0.0,
                lightshipKG     = doc.getDouble("lightshipKG")     ?: 0.0,
                numberOfHolds   = (doc.getLong("numberOfHolds")    ?: 0L).toInt(),
                displacement    = doc.getDouble("displacement")    ?: 0.0
            )
            Pair(vessel, doc.getBoolean("isEstimated") ?: true)
        } catch (e: Exception) { Pair(null, true) }
    }

    private suspend fun saveVesselToFirestore(vessel: Vessel, isEstimated: Boolean) {
        try {
            db.collection("vessels").document(vessel.imo)
                .set(hashMapOf(
                    "imo"             to vessel.imo,
                    "name"            to vessel.name,
                    "loa"             to vessel.loa,
                    "beam"            to vessel.beam,
                    "deadweight"      to vessel.deadweight,
                    "lightshipWeight" to vessel.lightshipWeight,
                    "lightshipKG"     to vessel.lightshipKG,
                    "numberOfHolds"   to vessel.numberOfHolds,
                    "displacement"    to vessel.displacement,
                    "isEstimated"     to isEstimated,
                    "registeredBy"    to (auth.currentUser?.uid ?: ""),
                    "lastUpdated"     to FieldValue.serverTimestamp()
                ), SetOptions.merge()).await()
        } catch (e: Exception) { Log.e("FIRESTORE", "Save: ${e.message}") }
    }

    private fun saveSearchHistory(imo: String, name: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(uid).collection("searchHistory")
                    .add(mapOf("imo" to imo, "name" to name, "searchedAt" to FieldValue.serverTimestamp()))
                    .await()
                loadSearchHistory()
            } catch (e: Exception) { Log.e("FIRESTORE", "History: ${e.message}") }
        }
    }

    fun loadSearchHistory() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snap = db.collection("users").document(uid).collection("searchHistory")
                    .orderBy("searchedAt", Query.Direction.DESCENDING).limit(5).get().await()
                searchHistory = snap.documents.mapNotNull { doc ->
                    SearchHistoryItem(doc.getString("imo") ?: return@mapNotNull null,
                        doc.getString("name") ?: return@mapNotNull null)
                }.distinctBy { it.imo }
            } catch (e: Exception) { Log.e("FIRESTORE", "History load: ${e.message}") }
        }
    }

    // ── Viagem / Météo ────────────────────────────────────────────────────────

    fun updateVoyage(departure: String, arrival: String, departureLat: Double? = null, departureLon: Double? = null, days: Int = 0) {
        currentVoyage = Voyage(departurePort = departure, arrivalPort = arrival, estimatedDays = days)
        if (departureLat != null && departureLon != null) updateCoordinates(departureLat, departureLon)
    }

    fun updateCoordinates(newLat: Double, newLon: Double) {
        latitude = newLat; longitude = newLon; fetchMarineData()
    }

    fun fetchMarineData() {
        viewModelScope.launch {
            isLoadingMarine = true; marineError = null
            try {
                marineData = RetrofitClient.weatherApi.getMarineWeather(latitude, longitude).hourly
            } catch (e: Exception) {
                marineData = null; marineError = "Could not fetch marine data. Check your connection."
            } finally { isLoadingMarine = false }
        }
    }

    // ── Portos ────────────────────────────────────────────────────────────────

    suspend fun searchPortsAsync(query: String): List<PortResult> {
        return try {
            RetrofitClient.portApi.searchPorts(whereClause = "UPPER(PORT_NAME) LIKE UPPER('%${query.replace("'","''")}%')")
                .features.mapNotNull { f -> val a = f.attributes
                    PortResult(a.PORT_NAME?.trim() ?: return@mapNotNull null, a.COUNTRY ?: "",
                        a.LATITUDE ?: return@mapNotNull null, a.LONGITUDE ?: return@mapNotNull null) }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getPortsByCountryAsync(countryCode: String): List<PortResult> {
        return try {
            RetrofitClient.portApi.searchPorts(whereClause = "COUNTRY='$countryCode'", limit = 60)
                .features.mapNotNull { f -> val a = f.attributes
                    PortResult(a.PORT_NAME?.trim() ?: return@mapNotNull null, a.COUNTRY ?: "",
                        a.LATITUDE ?: return@mapNotNull null, a.LONGITUDE ?: return@mapNotNull null) }
                .sortedBy { it.name }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getAvailableCountriesAsync(): List<String> {
        return try {
            RetrofitClient.portApi.getDistinctCountries().features.mapNotNull { it.attributes.COUNTRY }.distinct().sorted()
        } catch (e: Exception) { emptyList() }
    }

    // ── Fotos ─────────────────────────────────────────────────────────────────

    private fun fetchVesselPhoto(vesselName: String, imo: String = "") {
        if (vesselName.isBlank()) return
        viewModelScope.launch {
            isLoadingPhoto = true; vesselPhotoUrl = null
            try {
                if (imo.isNotBlank()) {
                    val cr = RetrofitClient.commonsApi.searchCommonsPhoto(search = "IMO $imo")
                    val cp = cr.query?.pages?.values?.filter { p ->
                        val t = p.imageinfo?.firstOrNull()?.thumburl?.lowercase() ?: ""
                        (p.pageid ?: -1) > 0 && t.isNotEmpty() && (t.endsWith(".jpg") || t.endsWith(".jpeg") || t.endsWith(".png"))
                    }?.maxByOrNull { it.pageid ?: 0 }
                    if (cp?.imageinfo?.firstOrNull()?.thumburl != null) { vesselPhotoUrl = cp.imageinfo?.firstOrNull()?.thumburl; return@launch }
                }
                val nr = RetrofitClient.wikiApi.searchVesselPhoto(search = "$vesselName ship")
                vesselPhotoUrl = nr.query?.pages?.values?.filter { p ->
                    val t = p.thumbnail?.source?.lowercase() ?: ""; val title = p.title?.lowercase() ?: ""
                    (p.pageid ?: -1) > 0 && t.isNotEmpty() && (t.endsWith(".jpg") || t.endsWith(".jpeg") || t.endsWith(".png")) &&
                            (title.contains("(ship)") || title.contains("(vessel)") || title.contains("(tanker)") ||
                                    title.contains("(bulk carrier)") || title.contains("(container ship)") || title.contains("(ferry)") ||
                                    title.contains("(cargo ship)") || title.startsWith("mv ") || title.startsWith("ms ") ||
                                    title.startsWith("ss ") || title.startsWith("mt "))
                }?.maxByOrNull { it.pageid ?: 0 }?.thumbnail?.source
            } catch (e: Exception) { vesselPhotoUrl = null } finally { isLoadingPhoto = false }
        }
    }

    // ── Tanques / Carga ───────────────────────────────────────────────────────

    fun updateTankWeight(tankName: String, newWeight: String) {
        val index = tanks.indexOfFirst { it.name == tankName }; if (index == -1) return
        val tank = tanks[index]; val weight = newWeight.toFloatOrNull()
        if (weight != null && tank.maxCapacity > 0 && weight > tank.maxCapacity) {
            tanks[index]    = tank.copy(currentWeight = tank.maxCapacity.toInt().toString())
            snackbarMessage = "${tank.name}: limited to ${tank.maxCapacity.toInt()} t (max capacity)"
        } else { tanks[index] = tank.copy(currentWeight = newWeight) }
    }

    fun updateTankCargo(tankName: String, cargo: CargoType) {
        val index = tanks.indexOfFirst { it.name == tankName }
        if (index != -1) tanks[index] = tanks[index].copy(selectedCargo = cargo)
    }

    // ── Estabilidade ──────────────────────────────────────────────────────────

    val currentKG: Double get() {
        val lsw = activeVessel.lightshipWeight; val lkg = activeVessel.lightshipKG
        val total = lsw + tanks.sumOf { it.weightFloat.toDouble() }; if (total <= 0) return 0.0
        val depth = if (activeVessel.beam > 0) activeVessel.beam / 1.65 else if (lkg > 0) lkg / 0.55 else 10.0
        return ((lsw * lkg) + tanks.sumOf { it.weightFloat.toDouble() * (it.selectedCargo.vcgFactor * depth) }) / total
    }

    val currentGM: Double get() {
        if (activeVessel.name.isEmpty() || activeVessel.lightshipWeight <= 0) return 0.0
        val disp = activeVessel.lightshipWeight + tanks.sumOf { it.weightFloat.toDouble() }
        if (disp <= 0 || activeVessel.loa <= 0 || activeVessel.beam <= 0) return 0.0
        val draft  = disp / (activeVessel.loa * activeVessel.beam * 0.75)
        val volume = disp / 1.025
        val bm     = (0.75 * activeVessel.loa * Math.pow(activeVessel.beam, 3.0) / 12.0) / volume
        return (draft / 2.0 + bm) - currentKG
    }
}