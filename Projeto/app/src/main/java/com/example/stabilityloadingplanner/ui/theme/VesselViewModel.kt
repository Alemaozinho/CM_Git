package com.example.stabilityloadingplanner.ui.theme

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stabilityloadingplanner.api.RetrofitClient
import com.example.stabilityloadingplanner.data.models.*
import kotlinx.coroutines.launch

class VesselViewModel : ViewModel() {
    var activeVessel by mutableStateOf(Vessel())
        private set
    var activeVesselHasEstimates by mutableStateOf(false)
        private set
    var searchResult by mutableStateOf<Vessel?>(null)
        private set
    var currentVoyage by mutableStateOf(Voyage())
        private set
    var latitude by mutableStateOf(38.69)
    var longitude by mutableStateOf(-9.31)
    var marineData by mutableStateOf<HourlyMarineData?>(null)
        private set
    var isLoadingMarine by mutableStateOf(false)
        private set
    var marineError by mutableStateOf<String?>(null)
        private set
    var vesselPhotoUrl by mutableStateOf<String?>(null)
        private set
    var isLoadingPhoto by mutableStateOf(false)
        private set
    var isSearchingVessel by mutableStateOf(false)
        private set
    var vesselSearchError by mutableStateOf<String?>(null)
        private set
    var searchHasEstimates by mutableStateOf(false)
        private set

    val waveSafetyLimit = 3.0
    val isSafetyRisk: Boolean get() = (marineData?.wave_height?.firstOrNull() ?: 0.0) > waveSafetyLimit
    val hasVesselSelected: Boolean get() = activeVessel.name.isNotEmpty()
    val searchResultIsComplete: Boolean get() = searchResult?.name?.isNotEmpty() == true

    val tanks = mutableStateListOf<Tank>()

    fun selectVessel(vessel: Vessel) {
        activeVessel             = vessel
        activeVesselHasEstimates = searchHasEstimates
        VesselDatabase.addVessel(vessel)
        tanks.clear()

        val forePeakCap = (vessel.deadweight * 0.05).toInt()
        val aftPeakCap  = (vessel.deadweight * 0.04).toInt()

        tanks.add(Tank(name = "Fore Peak", capacity = "${forePeakCap}t"))

        val remainingCapacity = vessel.deadweight - forePeakCap - aftPeakCap
        val validHolds   = if (vessel.numberOfHolds > 0) vessel.numberOfHolds else 1
        val holdCapacity = (remainingCapacity / validHolds).toInt()

        for (i in 1..validHolds) {
            tanks.add(Tank(name = "Hold $i", capacity = "${holdCapacity}t"))
        }

        tanks.add(Tank(name = "Aft Peak", capacity = "${aftPeakCap}t"))

        fetchVesselPhoto(vessel.name, vessel.imo)
    }

    fun updateActiveVesselDimensions(deadweight: Double, numberOfHolds: Int) {
        val loa  = if (activeVessel.loa  <= 0) 5.5 * Math.pow(deadweight, 0.35) else activeVessel.loa
        val beam = if (activeVessel.beam <= 0) (loa / 7.2).coerceIn(15.0, 70.0)  else activeVessel.beam

        val updated = activeVessel.copy(
            deadweight      = deadweight,
            numberOfHolds   = numberOfHolds,
            loa             = loa,
            beam            = beam,
            lightshipWeight = if (activeVessel.lightshipWeight <= 0) deadweight * 0.22 else activeVessel.lightshipWeight,
            lightshipKG     = if (activeVessel.lightshipKG     <= 0) beam       * 0.45 else activeVessel.lightshipKG
        )
        activeVessel             = updated
        activeVesselHasEstimates = false
        VesselDatabase.addVessel(updated)

        tanks.clear()
        val forePeakCap  = (deadweight * 0.05).toInt()
        val aftPeakCap   = (deadweight * 0.04).toInt()
        val remaining    = deadweight - forePeakCap - aftPeakCap
        val validHolds   = if (numberOfHolds > 0) numberOfHolds else 1
        val holdCapacity = (remaining / validHolds).toInt()

        tanks.add(Tank(name = "Fore Peak", capacity = "${forePeakCap}t"))
        for (i in 1..validHolds) tanks.add(Tank(name = "Hold $i", capacity = "${holdCapacity}t"))
        tanks.add(Tank(name = "Aft Peak", capacity = "${aftPeakCap}t"))
    }

    fun searchVesselByImo(imo: String) {
        vesselSearchError  = null
        searchHasEstimates = false
        viewModelScope.launch {
            isSearchingVessel = true
            try {
                val response = RetrofitClient.vesselApi.getVesselByImo(
                    auth = "Bearer ${RetrofitClient.VESSEL_API_KEY}",
                    id   = imo.trim()
                )
                val data = response.vessel
                if (data != null && !data.name.isNullOrBlank()) {
                    searchResult       = buildVesselFromApiData(imo, data)
                    searchHasEstimates = isEstimated(data)
                    fetchVesselPhoto(data.name, data.imo?.toString() ?: imo)
                    Log.d("VESSEL_API", "Found: ${data.name} (estimates=$searchHasEstimates)")
                } else {
                    fallbackToLocalDb(imo)
                }
            } catch (e: Exception) {
                Log.e("VESSEL_API", "Error: ${e.message}")
                fallbackToLocalDb(imo)
            } finally {
                isSearchingVessel = false
            }
        }
    }

    private fun buildVesselFromApiData(imo: String, data: VesselApiDetail): Vessel {
        val type = data.type ?: ""

        val dwt = data.dimensions?.deadweight?.takeIf { it > 0 }
            ?: estimateDwtFromGT(data.dimensions?.grossTonnage, type).takeIf { it > 0 }
            ?: estimateDwtFromType(type)

        val loa  = data.dimensions?.length?.takeIf { it > 0 } ?: (5.5 * Math.pow(dwt, 0.35))
        val beam = data.dimensions?.beam?.takeIf   { it > 0 } ?: (loa / 7.2).coerceIn(15.0, 70.0)

        val lightship   = dwt  * 0.22
        val lightshipKG = beam * 0.45
        val holds       = estimateHolds(type, dwt)

        return Vessel(
            imo             = data.imo?.toString() ?: imo,
            name            = data.name ?: "",
            loa             = loa,
            beam            = beam,
            displacement    = dwt + lightship,
            lightshipWeight = lightship,
            lightshipKG     = lightshipKG,
            deadweight      = dwt,
            numberOfHolds   = holds
        )
    }

    private fun isEstimated(data: VesselApiDetail): Boolean =
        data.dimensions == null ||
                (data.dimensions.deadweight ?: 0.0) <= 0 ||
                (data.dimensions.length     ?: 0.0) <= 0

    private fun estimateDwtFromGT(gt: Double?, type: String?): Double {
        if (gt == null || gt <= 0) return 0.0
        return when {
            type?.contains("container", ignoreCase = true) == true -> gt * 1.0
            type?.contains("tanker",    ignoreCase = true) == true -> gt * 1.8
            type?.contains("bulk",      ignoreCase = true) == true -> gt * 1.7
            else -> gt * 1.3
        }
    }

    private fun estimateDwtFromType(type: String): Double {
        return when {
            type.contains("container", ignoreCase = true) -> 60000.0
            type.contains("bulk",      ignoreCase = true) -> 75000.0
            type.contains("tanker",    ignoreCase = true) -> 100000.0
            type.contains("general",   ignoreCase = true) -> 15000.0
            type.contains("cargo",     ignoreCase = true) -> 20000.0
            type.contains("ferry",     ignoreCase = true) -> 8000.0
            type.contains("roro",      ignoreCase = true) -> 15000.0
            else -> 30000.0
        }
    }

    private fun estimateHolds(type: String, dwt: Double): Int {
        if (dwt <= 0) return 4
        return when {
            type.contains("bulk",      ignoreCase = true) -> (dwt / 15000).toInt().coerceIn(3, 10)
            type.contains("container", ignoreCase = true) -> (dwt / 18000).toInt().coerceIn(2,  8)
            type.contains("tanker",    ignoreCase = true) -> (dwt / 25000).toInt().coerceIn(2,  6)
            else -> (dwt / 15000).toInt().coerceIn(3, 8)
        }
    }

    private fun fallbackToLocalDb(imo: String) {
        searchResult = VesselDatabase.getByImo(imo.trim())
        if (searchResult == null) {
            vesselSearchError = "Vessel not found. Check the IMO or register manually."
        } else {
            fetchVesselPhoto(searchResult!!.name, searchResult!!.imo)
        }
    }

    fun updateVoyage(departure: String, arrival: String, departureLat: Double? = null, departureLon: Double? = null, days: Int = 0) {
        currentVoyage = Voyage(departurePort = departure, arrivalPort = arrival, estimatedDays = days)
        if (departureLat != null && departureLon != null) updateCoordinates(departureLat, departureLon)
    }

    fun updateCoordinates(newLat: Double, newLon: Double) {
        latitude  = newLat
        longitude = newLon
        fetchMarineData()
    }

    fun fetchMarineData() {
        viewModelScope.launch {
            isLoadingMarine = true
            marineError = null
            try {
                val response = RetrofitClient.weatherApi.getMarineWeather(latitude, longitude)
                marineData = response.hourly
                Log.d("API_SUCCESS", "Dados meteorológicos atualizados para Lat: $latitude, Lon: $longitude")
            } catch (e: Exception) {
                marineData  = null
                marineError = "Could not fetch marine data. Check your connection."
                Log.e("API_ERROR", "Erro a ir buscar meteorologia: ${e.message}")
            } finally {
                isLoadingMarine = false
            }
        }
    }

    suspend fun searchPortsAsync(query: String): List<PortResult> {
        val safeQuery = query.replace("'", "''")
        return try {
            val response = RetrofitClient.portApi.searchPorts(
                whereClause = "UPPER(PORT_NAME) LIKE UPPER('%$safeQuery%')"
            )
            response.features.mapNotNull { feature ->
                val attrs = feature.attributes
                val name  = attrs.PORT_NAME?.trim() ?: return@mapNotNull null
                val lat   = attrs.LATITUDE          ?: return@mapNotNull null
                val lon   = attrs.LONGITUDE         ?: return@mapNotNull null
                PortResult(name, attrs.COUNTRY ?: "", lat, lon)
            }
        } catch (e: Exception) {
            Log.e("PORTS", "Error: ${e.message}")
            emptyList()
        }
    }

    suspend fun getPortsByCountryAsync(countryCode: String): List<PortResult> {
        return try {
            val response = RetrofitClient.portApi.searchPorts(
                whereClause = "COUNTRY='$countryCode'",
                limit = 60
            )
            response.features.mapNotNull { feature ->
                val attrs = feature.attributes
                val name  = attrs.PORT_NAME?.trim() ?: return@mapNotNull null
                val lat   = attrs.LATITUDE          ?: return@mapNotNull null
                val lon   = attrs.LONGITUDE         ?: return@mapNotNull null
                PortResult(name, attrs.COUNTRY ?: "", lat, lon)
            }.sortedBy { it.name }
        } catch (e: Exception) {
            Log.e("PORTS", "Error: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAvailableCountriesAsync(): List<String> {
        return try {
            val response = RetrofitClient.portApi.getDistinctCountries()
            response.features.mapNotNull { it.attributes.COUNTRY }.distinct().sorted()
        } catch (e: Exception) {
            Log.e("PORTS", "Error: ${e.message}")
            emptyList()
        }
    }

    // Três tentativas por ordem de precisão:
    // 1. Wikimedia Commons por IMO (ficheiros com IMO no nome — muito preciso)
    // 2. Wikipedia por IMO (artigos que mencionam o IMO no texto)
    // 3. Wikipedia por nome com filtro estrito de título
    private fun fetchVesselPhoto(vesselName: String, imo: String = "") {
        if (vesselName.isBlank()) return
        viewModelScope.launch {
            isLoadingPhoto = true
            vesselPhotoUrl = null
            try {
                // Tentativa 1 — Wikimedia Commons por IMO
                if (imo.isNotBlank()) {
                    val commonsResponse = RetrofitClient.commonsApi.searchCommonsPhoto(search = "IMO $imo")
                    val commonsPage = commonsResponse.query?.pages?.values
                        ?.filter { page ->
                            val pageid   = page.pageid ?: -1
                            val thumbUrl = page.imageinfo?.firstOrNull()?.thumburl?.lowercase() ?: ""
                            pageid > 0 &&
                                    !page.imageinfo.isNullOrEmpty() &&
                                    (thumbUrl.endsWith(".jpg") || thumbUrl.endsWith(".jpeg") || thumbUrl.endsWith(".png"))
                        }
                        ?.maxByOrNull { it.pageid ?: 0 }

                    if (commonsPage != null) {
                        vesselPhotoUrl = commonsPage.imageinfo?.firstOrNull()?.thumburl
                        Log.d("WIKI", "Commons photo by IMO '$imo': $vesselPhotoUrl")
                        return@launch
                    }
                }

                // Tentativa 2 — Wikipedia por IMO
                if (imo.isNotBlank()) {
                    val imoResponse = RetrofitClient.wikiApi.searchVesselPhoto(search = "IMO $imo")
                    val imoPage = imoResponse.query?.pages?.values
                        ?.filter { page ->
                            val pageid   = page.pageid ?: -1
                            val thumbUrl = page.thumbnail?.source?.lowercase() ?: ""
                            pageid > 0 && page.thumbnail != null &&
                                    (thumbUrl.endsWith(".jpg") || thumbUrl.endsWith(".jpeg") || thumbUrl.endsWith(".png"))
                        }
                        ?.maxByOrNull { it.pageid ?: 0 }

                    if (imoPage != null) {
                        vesselPhotoUrl = imoPage.thumbnail?.source
                        Log.d("WIKI", "Wikipedia photo by IMO '$imo': $vesselPhotoUrl")
                        return@launch
                    }
                }

                // Tentativa 3 — Wikipedia por nome com filtro estrito de título
                val nameResponse = RetrofitClient.wikiApi.searchVesselPhoto(search = "$vesselName ship")
                val namePage = nameResponse.query?.pages?.values
                    ?.filter { page ->
                        val pageid   = page.pageid ?: -1
                        val thumbUrl = page.thumbnail?.source?.lowercase() ?: ""
                        val title    = page.title?.lowercase() ?: ""
                        pageid > 0 && page.thumbnail != null &&
                                (thumbUrl.endsWith(".jpg") || thumbUrl.endsWith(".jpeg") || thumbUrl.endsWith(".png")) &&
                                (title.contains("(ship)") || title.contains("(vessel)") ||
                                        title.contains("(tanker)") || title.contains("(bulk carrier)") ||
                                        title.contains("(container ship)") || title.contains("(ferry)") ||
                                        title.contains("(cargo ship)") || title.startsWith("mv ") ||
                                        title.startsWith("ms ") || title.startsWith("ss ") ||
                                        title.startsWith("mt ") || title.contains(" mv ") || title.contains(" ms "))
                    }
                    ?.maxByOrNull { it.pageid ?: 0 }

                vesselPhotoUrl = namePage?.thumbnail?.source
                Log.d("WIKI", "Wikipedia photo by name '$vesselName': $vesselPhotoUrl")

            } catch (e: Exception) {
                vesselPhotoUrl = null
                Log.e("WIKI", "Error fetching photo: ${e.message}")
            } finally {
                isLoadingPhoto = false
            }
        }
    }

    fun updateTankWeight(tankName: String, newWeight: String) {
        val index = tanks.indexOfFirst { it.name == tankName }
        if (index != -1) tanks[index] = tanks[index].copy(currentWeight = newWeight)
    }

    fun updateTankCargo(tankName: String, cargo: CargoType) {
        val index = tanks.indexOfFirst { it.name == tankName }
        if (index != -1) tanks[index] = tanks[index].copy(selectedCargo = cargo)
    }

    val currentKG: Double get() {
        val lightshipWeight = activeVessel.lightshipWeight
        val lightshipKG     = activeVessel.lightshipKG
        val totalWeight     = lightshipWeight + tanks.sumOf { it.weightFloat.toDouble() }
        if (totalWeight <= 0) return 0.0
        val totalMoment = (lightshipWeight * lightshipKG) + tanks.sumOf { it.weightFloat.toDouble() * it.selectedCargo.vcgFactor }
        return totalMoment / totalWeight
    }

    val currentGM: Double get() {
        if (activeVessel.name.isEmpty() || activeVessel.lightshipWeight <= 0) return 0.0
        val totalDisplacement = activeVessel.lightshipWeight + tanks.sumOf { it.weightFloat.toDouble() }
        if (totalDisplacement <= 0) return 0.0
        if (activeVessel.loa <= 0 || activeVessel.beam <= 0) return 0.0
        val estimatedDraft = totalDisplacement / (activeVessel.loa * activeVessel.beam * 0.75)
        val kb = estimatedDraft / 2.0
        val volume = totalDisplacement / 1.025
        val bm = (activeVessel.loa * Math.pow(activeVessel.beam, 3.0) / 12.0) / volume
        return (kb + bm) - currentKG
    }
}