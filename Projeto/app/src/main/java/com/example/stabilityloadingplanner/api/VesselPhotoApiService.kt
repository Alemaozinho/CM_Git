package com.example.stabilityloadingplanner.api

import com.example.stabilityloadingplanner.data.models.WikiCommonsResponse
import com.example.stabilityloadingplanner.data.models.WikiSearchImageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface VesselPhotoApiService {

    // Pesquisa de artigos no Wikipedia — para navios com artigo próprio
    @GET("w/api.php")
    suspend fun searchVesselPhoto(
        @Query("action")      action:    String = "query",
        @Query("generator")   generator: String = "search",
        @Query("gsrsearch")   search:    String,
        @Query("gsrlimit")    limit:     Int    = 3,
        @Query("prop")        prop:      String = "pageimages",
        @Query("pithumbsize") thumbSize: Int    = 600,
        @Query("format")      format:    String = "json"
    ): WikiSearchImageResponse

    // Pesquisa de ficheiros de imagem na Wikimedia Commons por IMO
    @GET("w/api.php")
    suspend fun searchCommonsPhoto(
        @Query("action")       action:     String = "query",
        @Query("generator")    generator:  String = "search",
        @Query("gsrsearch")    search:     String,
        @Query("gsrnamespace") namespace:  Int    = 6,
        @Query("gsrlimit")     limit:      Int    = 5,
        @Query("prop")         prop:       String = "imageinfo",
        @Query("iiprop")       iiprop:     String = "url",
        @Query("iiurlwidth")   thumbWidth: Int    = 600,
        @Query("format")       format:     String = "json"
    ): WikiCommonsResponse
}