package com.example.stabilityloadingplanner.api

import com.example.stabilityloadingplanner.data.models.PortQueryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PortApiService {

    @GET("query")
    suspend fun searchPorts(
        @Query("where") whereClause: String,
        @Query("outFields") outFields: String = "PORT_NAME,COUNTRY,LATITUDE,LONGITUDE,HARBORSIZE",
        @Query("returnGeometry") returnGeometry: Boolean = false,
        @Query("resultRecordCount") limit: Int = 15,
        @Query("f") format: String = "json"
    ): PortQueryResponse

    @GET("query")
    suspend fun getDistinctCountries(
        @Query("where") whereClause: String = "1=1",
        @Query("outFields") outFields: String = "COUNTRY",
        @Query("returnDistinctValues") distinct: Boolean = true,
        @Query("returnGeometry") returnGeometry: Boolean = false,
        @Query("orderByFields") orderBy: String = "COUNTRY",
        @Query("resultRecordCount") limit: Int = 300,
        @Query("f") format: String = "json"
    ): PortQueryResponse
}