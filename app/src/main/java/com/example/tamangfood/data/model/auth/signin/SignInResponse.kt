package com.example.tamangfood.data.model.auth.signin


data class SignInResponse(
    val code: Int,
    val message: String,
    val result: Result
)

data class Result(
    val id: Int,
    val fullName: String,
    val email: String,
    val token: Token
)

data class Token(
    val accessToken: String,
    val refreshToken: String
)

