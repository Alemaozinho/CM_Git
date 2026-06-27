package com.example.stabilityloadingplanner.data.models

data class PortQueryResponse(
    val features: List<PortFeature> = emptyList()
)

data class PortFeature(
    val attributes: PortAttributes
)

data class PortAttributes(
    val PORT_NAME: String?,
    val COUNTRY: String?,
    val LATITUDE: Double?,
    val LONGITUDE: Double?,
    val HARBORSIZE: String?
)

data class PortResult(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double
)