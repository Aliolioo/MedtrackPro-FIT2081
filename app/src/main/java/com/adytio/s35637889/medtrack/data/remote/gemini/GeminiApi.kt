package com.adytio.s35637889.medtrack.data.remote.gemini

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateTip(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}