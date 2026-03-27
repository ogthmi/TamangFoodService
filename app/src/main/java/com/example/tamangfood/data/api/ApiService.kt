package com.example.tamangfood.data.api

import com.example.tamangfood.data.model.auth.signin.SignInRequest
import com.example.tamangfood.data.model.auth.signin.SignInResponse
import com.example.tamangfood.data.model.auth.signup.SignUpRequest
import com.example.tamangfood.data.model.auth.signup.SignUpResponse
import com.example.tamangfood.data.model.sample.SampleRequest
import com.example.tamangfood.data.model.sample.SampleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // Sample
    @POST("api/v1/...")
    suspend fun sample(
        @Body request: SampleRequest
    ): SampleResponse

    // Sign up
    @POST("api/v1/auth/sign-up")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    // Sign in
    @POST("api/v1/auth/log-in")
    suspend fun signIn(
        @Body request: SignInRequest
    ): Response<SignInResponse>


}