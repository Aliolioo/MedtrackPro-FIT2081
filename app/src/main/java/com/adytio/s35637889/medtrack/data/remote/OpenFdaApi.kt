package com.adytio.s35637889.medtrack.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenFdaApi {

    @GET("drug/label.json")
    suspend fun searchDrugLabel(
        @Query("search") search: String,
        @Query("limit") limit: Int = 1
    ): OpenFdaResponse
}