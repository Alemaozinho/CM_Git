package com.example.coolweathergallery

data class WeatherData(val hourly: HourlyData)
data class HourlyData(val time: List<String>, val temperature_2m: List<Float>, val weathercode: List<Int>)
data class WeatherItem(val time: String, val temp: String, val imageResId: Int)