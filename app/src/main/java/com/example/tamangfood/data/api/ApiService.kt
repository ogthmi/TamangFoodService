package com.example.tamangfood.data.api

import com.example.tamangfood.data.model.sample.SampleRequest
import com.example.tamangfood.data.model.sample.SampleResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // Sample
    @POST("api/v1/...")
    suspend fun sample(
        @Body request: SampleRequest
    ): SampleResponse
}