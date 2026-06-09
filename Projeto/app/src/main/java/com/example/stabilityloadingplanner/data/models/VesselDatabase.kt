package com.example.stabilityloadingplanner.data.models

object VesselDatabase {
    // MutableListOf para podermos adicionar os navios registados pelo utilizador
    private val ships = mutableListOf(
        Vessel(imo = "9432866", name = "MSC Beatrice", loa = 366.0, beam = 51.0, displacement = 176882.0, lightshipWeight = 55000.0, lightshipKG = 12.5, deadweight = 121800.0, numberOfHolds = 8),
        Vessel(imo = "9321483", name = "Emma Maersk", loa = 397.0, beam = 56.0, displacement = 170974.0, lightshipWeight = 50000.0, lightshipKG = 11.2, deadweight = 120000.0, numberOfHolds = 7),
        Vessel(imo = "1234567", name = "Demo Ship", loa = 100.0, beam = 20.0, displacement = 5000.0, lightshipWeight = 1500.0, lightshipKG = 5.0, deadweight = 3500.0, numberOfHolds = 3)
    )

    fun getByImo(imo: String): Vessel? = ships.find { it.imo == imo }

    // Função para gravar o navio registado na base de dados em memória
    fun addVessel(newVessel: Vessel) {
        // Verifica se o IMO já existe. Se não existir, adiciona à lista.
        val exists = ships.any { it.imo == newVessel.imo }
        if (!exists) {
            ships.add(newVessel)
        }
    }
}