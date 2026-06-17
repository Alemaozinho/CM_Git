package com.example.stabilityloadingplanner.ui.theme

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stabilityloadingplanner.api.RetrofitClient
import com.example.stabilityloadingplanner.data.models.*
import kotlinx.coroutines.launch

data class CargoType(val name: String, val vcgFactor: Double)
val CargoOptions = listOf(
    CargoType("General Cargo", 2.0),
    CargoType("Grain (Bulk)", 3.5),
    CargoType("Steel Coils", 1.2),
    CargoType("Container", 4.0)
)

data class Tank(
    val name: String,
    val capacity: String,
    var currentWeight: String = "",
    var selectedCargo: CargoType = CargoOptions[0]
) {
    val maxCapacity: Float get() = capacity.replace("t", "").toFloatOrNull() ?: 0f
    val weightFloat: Float get() = currentWeight.toFloatOrNull() ?: 0f
    val isOverloaded: Boolean get() = weightFloat > maxCapacity
}

class VesselViewModel : ViewModel() {
    var activeVessel by mutableStateOf(Vessel())
        private set
    var searchResult by mutableStateOf<Vessel?>(null)
        private set
    var currentVoyage by mutableStateOf(Voyage())
        private set
    var latitude by mutableStateOf(38.69)
    var longitude by mutableStateOf(-9.31)
    var marineData by mutableStateOf<HourlyMarineData?>(null)
        private set

    val waveSafetyLimit = 3.0
    val isSafetyRisk: Boolean get() = (marineData?.wave_height?.firstOrNull() ?: 0.0) > waveSafetyLimit

    val tanks = mutableStateListOf<Tank>()

    private val portCoordinates = mapOf(
        "Port of Lisbon, PT" to Pair(38.71, -9.14),
        "Port of Sines, PT" to Pair(37.95, -8.88),
        "Port of Leixões, PT" to Pair(41.18, -8.70),
        "Port of Rotterdam, NL" to Pair(51.95, 4.05),
        "Port of Antwerp, BE" to Pair(51.28, 4.38),
        "Port of Hamburg, DE" to Pair(53.54, 9.96),
        "Port of Algeciras, ES" to Pair(36.13, -5.44)
    )

    fun registerVessel(vessel: Vessel) { activeVessel = vessel }

    fun selectVessel(vessel: Vessel) {
        activeVessel = vessel

        // Guardar o navio na VesselDatabase (se for novo)
        VesselDatabase.addVessel(vessel)

        // Limpar os tanques do navio anterior
        tanks.clear()

        // Calcular capacidades proporcionais ao tamanho do navio (Deadweight)
        val forePeakCap = (vessel.deadweight * 0.05).toInt() // 5% do peso total
        val aftPeakCap = (vessel.deadweight * 0.04).toInt()  // 4% do peso total

        // Adicionar o Fore Peak dinâmico
        tanks.add(Tank(name = "Fore Peak", capacity = "${forePeakCap}t"))

        // O resto da capacidade é dividida igualmente pelos porões (Holds)
        val remainingCapacity = vessel.deadweight - forePeakCap - aftPeakCap

        // Caso o numberOfHolds seja 0 ou inválido, evitamos divisão por zero
        val validHolds = if (vessel.numberOfHolds > 0) vessel.numberOfHolds else 1
        val holdCapacity = (remainingCapacity / validHolds).toInt()

        for (i in 1..validHolds) {
            tanks.add(Tank(name = "Hold $i", capacity = "${holdCapacity}t"))
        }

        // Adicionar o Aft Peak dinâmico
        tanks.add(Tank(name = "Aft Peak", capacity = "${aftPeakCap}t"))
    }

    fun searchVesselByImo(imo: String) { searchResult = VesselDatabase.getByImo(imo.trim()) }

    fun updateVoyage(departure: String, arrival: String, days: Int) {
        // Criar a viagem apenas com os parâmetros que o modelo atual suporta
        currentVoyage = Voyage(departure, arrival)

        val coords = portCoordinates[departure]
        if (coords != null) {
            updateCoordinates(coords.first, coords.second)
        }
    }

    fun updateCoordinates(newLat: Double, newLon: Double) {
        latitude = newLat
        longitude = newLon
        fetchMarineData()
    }

    fun fetchMarineData() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.weatherApi.getMarineWeather(latitude, longitude)
                marineData = response.hourly
                Log.d("API_SUCCESS", "Dados meteorológicos atualizados para Lat: $latitude, Lon: $longitude")
            }
            catch (e: Exception) {
                marineData = null
                Log.e("API_ERROR", "Erro a ir buscar meteorologia: ${e.message}")
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
        val lightshipKG = activeVessel.lightshipKG
        val totalWeight = lightshipWeight + tanks.sumOf { it.weightFloat.toDouble() }

        if (totalWeight <= 0) return 0.0
        val totalMoment = (lightshipWeight * lightshipKG) + tanks.sumOf { it.weightFloat.toDouble() * it.selectedCargo.vcgFactor }
        return totalMoment / totalWeight
    }

    val currentGM: Double get() {
        // Trava de segurança: se não tiver navio ou peso, GM é 0
        if (activeVessel.name.isEmpty() || activeVessel.lightshipWeight <= 0) {
            return 0.0
        }

        val totalDisplacement = activeVessel.lightshipWeight + tanks.sumOf { it.weightFloat.toDouble() }
        if (totalDisplacement <= 0) return 0.0

        // KB estimado
        val estimatedDraft = totalDisplacement / (activeVessel.loa * activeVessel.beam * 0.75)
        val kb = estimatedDraft / 2.0

        // BM = I / V
        val waterDensity = 1.025
        val volume = totalDisplacement / waterDensity
        val bm = (activeVessel.loa * Math.pow(activeVessel.beam, 3.0) / 12.0) / volume
        val kmt = kb + bm
        return kmt - currentKG
    }
}