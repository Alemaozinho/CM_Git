package com.example.stabilityloadingplanner.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

// ── Modelos de Request ────────────────────────────────────────────────────────

data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiConfig = GeminiConfig()
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

data class GeminiPart(val text: String)

data class GeminiConfig(
    val temperature: Double = 0.1,
    val maxOutputTokens: Int = 300
    // NOTA: responseMimeType REMOVIDO — causa erros 400 no tier gratuito
)

// ── Modelos de Response ───────────────────────────────────────────────────────

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)

// ── Interface Retrofit ────────────────────────────────────────────────────────

interface GeminiApiService {
    // gemini-2.0-flash — modelo gratuito actual (1500 req/dia no tier free)
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}