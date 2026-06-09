package com.example.stabilityloadingplanner.data.models

data class Voyage(
    val departurePort: String = "",
    val arrivalPort: String = "",
    val estimatedDays: Int = 0,
    val departure: String = "Not Set",
    val arrival: String = "Not Set"
)