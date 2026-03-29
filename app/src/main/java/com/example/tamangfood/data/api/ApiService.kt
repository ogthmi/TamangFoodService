package com.example.tamangfood.data.api

import com.example.tamangfood.data.model.auth.forgotpassword.RequestOtpResponse
import com.example.tamangfood.data.model.auth.forgotpassword.ResetPasswordRequest
import com.example.tamangfood.data.model.auth.forgotpassword.ResetPasswordResponse
import com.example.tamangfood.data.model.auth.signin.SignInRequest
import com.example.tamangfood.data.model.auth.signin.SignInResponse
import com.example.tamangfood.data.model.auth.signup.SignUpRequest
import com.example.tamangfood.data.model.auth.signup.SignUpResponse
import com.example.tamangfood.data.model.sample.SampleRequest
import com.example.tamangfood.data.model.sample.SampleResponse
import com.example.tamangfood.data.model.user.changepassword.ChangePasswordRequest
import com.example.tamangfood.data.model.user.changepassword.ChangePasswordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

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

    @POST("api/v1/auth/OTP/{email}")
    suspend fun requestOTP(
        @Path(value = "email") email: String,
    ): Response<RequestOtpResponse>

    @POST("api/v1/auth/forgot-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

    @POST("api/v1/auth/change-password")
    suspend fun changePassword(
        @Body body: ChangePasswordRequest
    ): Response<ChangePasswordResponse>

    @DELETE("api/v1/users/{id}")
    suspend fun deleteUser(
        @Path("id") userId: Int
    ): Response<Void>
}