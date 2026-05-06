package com.example.cooljetpackweatherapp.data

import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val current_weather: CurrentWeather? = null,
    val hourly: HourlyData? = null
)

@Serializable
data class CurrentWeather(
    val temperature: Float = 0f,
    val windspeed: Float = 0f,
    val winddirection: Int = 0,
    val time: String = ""
)

@Serializable
data class HourlyData(
    val pressure_msl: List<Float> = emptyList()
)