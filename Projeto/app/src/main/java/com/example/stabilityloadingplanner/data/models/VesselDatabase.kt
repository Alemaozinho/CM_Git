package com.example.stabilityloadingplanner.data.models

object VesselDatabase {
    val ships = listOf(
        Vessel(imo = "9432866", name = "MSC Beatrice", loa = 366.0, beam = 51.0, displacement = 176882.0, lightshipWeight = 55000.0, lightshipKG = 12.5, deadweight = 121800.0),
        Vessel(imo = "9321483", name = "Emma Maersk", loa = 397.0, beam = 56.0, displacement = 170974.0, lightshipWeight = 50000.0, lightshipKG = 11.2, deadweight = 120000.0),
        Vessel(imo = "1234567", name = "Demo Ship", loa = 100.0, beam = 20.0, displacement = 5000.0, lightshipWeight = 1500.0, lightshipKG = 5.0, deadweight = 3500.0)
    )

    fun getByImo(imo: String): Vessel? = ships.find { it.imo == imo }
}