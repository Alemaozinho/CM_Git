package com.example.stabilityloadingplanner

import android.app.Application
import coil.Coil
import coil.ImageLoader
import okhttp3.OkHttpClient

class StabilityApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Wikimedia exige User-Agent também nas imagens directas — sem isto o Coil recebe 403
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .okHttpClient {
                    OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            val request = chain.request().newBuilder()
                                .header("User-Agent", "StabilityLoadingPlanner/1.0 (Android; academic project)")
                                .build()
                            chain.proceed(request)
                        }
                        .build()
                }
                .build()
        )
    }
}