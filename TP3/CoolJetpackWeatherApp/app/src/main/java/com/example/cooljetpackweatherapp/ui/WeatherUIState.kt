package com.example.cooljetpackweatherapp.ui

data class WeatherUIState(
    val latitude: Float = 38.7223f,
    val longitude: Float = -9.1393f,
    val temperature: Float = 0.0f,
    val windspeed: Float = 0.0f,
    val winddirection: Int = 0,
    val weathercode: Int = 0,
    val seaLevelPressure: Float = 0.0f,
    val time: String = "N/A"
)