package com.example.stabilityloadingplanner.data.models

data class MarineWeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: HourlyMarineData
)

data class HourlyMarineData(
    val time: List<String>,
    val wave_height: List<Double>?,
    val wave_period: List<Double>?,
    val wind_wave_height: List<Double>?,
    val sea_surface_temperature: List<Double>?,
    val ocean_current_velocity: List<Double>?
)