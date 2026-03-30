package com.example.tamangfood.data.model.auth.refresh

import com.example.tamangfood.data.model.auth.signin.Token

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val code: Int,
    val message: String,
    val result: RefreshTokenResult?
)

data class RefreshTokenResult(
    val accessToken: String,
    val refreshToken: String,
)
