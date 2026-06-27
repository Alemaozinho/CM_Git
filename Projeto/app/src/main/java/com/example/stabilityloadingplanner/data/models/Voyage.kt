package com.example.stabilityloadingplanner.data.models

data class Voyage(
    val departurePort: String = "Not Set",
    val arrivalPort: String = "Not Set",
    val estimatedDays: Int = 0
)