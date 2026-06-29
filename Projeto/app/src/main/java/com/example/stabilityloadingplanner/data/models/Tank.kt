package com.example.stabilityloadingplanner.data.models

// vcgFactor = fracção da profundidade do navio (D = beam / 1.65)
// VCG real do cargo (em metros) = vcgFactor × D
// Isto dá valores realistas de KG para cada tipo de carga
data class CargoType(val name: String, val vcgFactor: Double)

val CargoOptions = listOf(
    CargoType("Iron Ore",       0.12),  // Muito pesado — fundo dos porões
    CargoType("Steel Coils",    0.18),  // Pesado — estivagem baixa
    CargoType("Coal",           0.30),  // Granel — porões a meio
    CargoType("Grain",          0.33),  // Granel — porões a meio
    CargoType("General Cargo",  0.48),  // Misto — altura média
    CargoType("Containers",     1.05),  // Empilhados em convés — risco!
    CargoType("Timber / Logs",  1.30),  // Carga de convés — instabilidade!
    CargoType("Ballast Water",  0.06)   // Duplo fundo — muito baixo
)

data class Tank(
    val name:          String,
    val capacity:      String    = "0t",
    val currentWeight: String    = "",
    val selectedCargo: CargoType = CargoOptions[4] // General Cargo por defeito
) {
    val maxCapacity: Float
        get() = capacity.replace("t", "").toFloatOrNull() ?: 0f

    val weightFloat: Float
        get() = currentWeight.toFloatOrNull() ?: 0f

    val isOverloaded: Boolean
        get() = maxCapacity > 0 && weightFloat > maxCapacity
}
