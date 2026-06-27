package com.example.stabilityloadingplanner.data.models

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