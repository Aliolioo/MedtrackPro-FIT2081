package com.adytio.s35637889.medtrack.data.remote.gemini

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiRetrofit {

    val api: GeminiApi by lazy {

        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
    }
}