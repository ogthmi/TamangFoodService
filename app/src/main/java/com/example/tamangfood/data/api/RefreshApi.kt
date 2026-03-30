package com.example.tamangfood.data.api

import com.example.tamangfood.data.model.auth.refresh.RefreshTokenRequest
import com.example.tamangfood.data.model.auth.refresh.RefreshTokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Gọi refresh **không** qua [AuthInterceptor] (client OkHttp riêng) để tránh đệ quy.
 */
interface RefreshApi {
    @POST("api/v1/auth/refresh")
    fun refresh(@Body body: RefreshTokenRequest): Call<RefreshTokenResponse>
}
