package com.example.stabilityloadingplanner.api

import com.example.stabilityloadingplanner.data.models.VesselApiDetailResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface VesselApiService {

    // GET /v1/vessel/{id}?filter.idType=imo — endpoint correcto da documentação
    @GET("v1/vessel/{id}")
    suspend fun getVesselByImo(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
        @Query("filter.idType") idType: String = "imo"
    ): VesselApiDetailResponse
}