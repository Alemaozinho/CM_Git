package com.example.stabilityloadingplanner.api

import com.example.stabilityloadingplanner.BuildConfig
import com.example.stabilityloadingplanner.data.models.MarineWeatherResponse
import okhttp3.OkHttpClient
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
    private const val BASE_URL         = "https://marine-api.open-meteo.com/"
    private const val PORT_BASE_URL    = "https://services9.arcgis.com/j1CY4yzWfwptbTWN/arcgis/rest/services/WorldPortIndex_WFL1/FeatureServer/0/"
    private const val WIKI_BASE_URL    = "https://en.wikipedia.org/"
    private const val COMMONS_BASE_URL = "https://commons.wikimedia.org/"
    private const val VESSEL_BASE_URL  = "https://api.vesselapi.com/"

    val VESSEL_API_KEY: String get() = BuildConfig.VESSEL_API_KEY

    private val wikiHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "StabilityLoadingPlanner/1.0 (Android; academic project)")
                .build()
            chain.proceed(request)
        }
        .build()

    val weatherApi: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)
    }

    val portApi: PortApiService by lazy {
        Retrofit.Builder()
            .baseUrl(PORT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PortApiService::class.java)
    }

    val wikiApi: VesselPhotoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(WIKI_BASE_URL)
            .client(wikiHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VesselPhotoApiService::class.java)
    }

    val commonsApi: VesselPhotoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(COMMONS_BASE_URL)
            .client(wikiHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VesselPhotoApiService::class.java)
    }

    val vesselApi: VesselApiService by lazy {
        Retrofit.Builder()
            .baseUrl(VESSEL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VesselApiService::class.java)
    }
}