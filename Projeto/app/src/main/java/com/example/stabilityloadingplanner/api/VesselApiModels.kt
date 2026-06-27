package com.example.stabilityloadingplanner.data.models

import com.google.gson.annotations.SerializedName

data class VesselApiDetailResponse(
    @SerializedName(value = "vessel", alternate = ["data", "result", "ship"])
    val vessel: VesselApiDetail?
)

data class VesselApiDetail(
    @SerializedName(value = "name", alternate = ["vesselName", "vessel_name", "shipName"])
    val name: String?,
    @SerializedName(value = "imo", alternate = ["imoNumber", "imo_number"])
    val imo: Long?,
    val mmsi: Long?,
    @SerializedName(value = "flag", alternate = ["flagState", "flag_state", "country"])
    val flag: String?,
    @SerializedName(value = "type", alternate = ["vesselType", "vessel_type", "shipType"])
    val type: String?,
    val dimensions: VesselDimensions?,
    @SerializedName(value = "yearBuilt", alternate = ["year_built", "built"])
    val yearBuilt: Int?
)

data class VesselDimensions(
    @SerializedName(value = "length", alternate = ["loa", "lengthOverall", "length_overall"])
    val length: Double?,
    @SerializedName(value = "beam", alternate = ["breadth", "width"])
    val beam: Double?,
    @SerializedName(value = "deadweight", alternate = ["dwt", "deadweightTonnage", "deadweight_tonnage"])
    val deadweight: Double?,
    val draft: Double?,
    @SerializedName(value = "grossTonnage", alternate = ["gt", "gross_tonnage", "grt"])
    val grossTonnage: Double?
)