package com.adytio.s35637889.medtrack.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val openFdaApi: OpenFdaApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.fda.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFdaApi::class.java)
    }
}