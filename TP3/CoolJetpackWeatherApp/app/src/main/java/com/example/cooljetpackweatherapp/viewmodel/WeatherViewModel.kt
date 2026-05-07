package com.example.cooljetpackweatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooljetpackweatherapp.data.WeatherApiClient
import com.example.cooljetpackweatherapp.ui.WeatherUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    fun updateLatitude(lat: Float) {
        _uiState.update { it.copy(latitude = lat) }
    }

    fun updateLongitude(lon: Float) {
        _uiState.update { it.copy(longitude = lon) }
    }

    // Função que liga ao WeatherApiClient para buscar dados reais
    fun fetchWeather() {
        viewModelScope.launch {
            try {
                val data = WeatherApiClient.getWeather(_uiState.value.latitude, _uiState.value.longitude)
                if (data != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            temperature = data.current_weather?.temperature ?: 0f,
                            windspeed = data.current_weather?.windspeed ?: 0f,
                            seaLevelPressure = data.hourly?.pressure_msl?.firstOrNull() ?: 0f,
                            time = data.current_weather?.time ?: "N/A"
                        )
                    }
                }
            } catch (e: Exception) {
                // Se falhar a net ou a API, o erro aparece no Logcat para eu ver
                e.printStackTrace()
            }
        }
    }
}