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

    val tanks = mutableStateListOf(
        Tank("Fore Peak", "500t"), Tank("Hold 1", "2500t"),
        Tank("Hold 2", "2500t"), Tank("Aft Peak", "400t")
    )

    // NOVO: Dicionário que liga o nome do porto às suas coordenadas (Latitude, Longitude)
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
    fun selectVessel(vessel: Vessel) { activeVessel = vessel; searchResult = null }
    fun searchVesselByImo(imo: String) { searchResult = VesselDatabase.getByImo(imo.trim()) }

    // ATUALIZADO: Quando mudas a viagem, ele vai buscar as coordenadas do porto de partida
    fun updateVoyage(departure: String, arrival: String, days: Int) {
        currentVoyage = Voyage(departure, arrival, days)

        // Verifica se o porto de partida existe no nosso dicionário
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
        val lightshipWeight = activeVessel.lightshipWeight.toDouble()
        val lightshipKG = activeVessel.lightshipKG.toDouble()
        val totalWeight = lightshipWeight + tanks.sumOf { it.weightFloat.toDouble() }

        if (totalWeight <= 0) return 0.0
        val totalMoment = (lightshipWeight * lightshipKG) + tanks.sumOf { it.weightFloat.toDouble() * it.selectedCargo.vcgFactor }
        return totalMoment / totalWeight
    }

    val currentGM: Double get() = 6.0 - currentKG
}