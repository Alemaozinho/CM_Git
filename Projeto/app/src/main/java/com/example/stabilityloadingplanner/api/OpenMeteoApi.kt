package com.example.stabilityloadingplanner.api

import com.example.stabilityloadingplanner.data.models.MarineWeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    @GET("v1/marine")
    suspend fun getMarineWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("hourly") hourly: String = "wave_height,wave_period,wind_wave_height,sea_surface_temperature,ocean_current_velocity"
    ): MarineWeatherResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://marine-api.open-meteo.com/"

    val weatherApi: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)
    }
}