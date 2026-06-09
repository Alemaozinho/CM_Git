package com.example.stabilityloadingplanner.data.models

data class Vessel(
    val imo: String = "",
    val name: String = "",
    val loa: Double = 0.0,
    val beam: Double = 0.0,
    val displacement: Double = 0.0,
    val lightshipWeight: Double = 0.0,
    val lightshipKG: Double = 0.0,
    val deadweight: Double = 0.0,
    val numberOfHolds: Int = 0
)