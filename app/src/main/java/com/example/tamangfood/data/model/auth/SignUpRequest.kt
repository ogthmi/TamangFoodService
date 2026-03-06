package com.example.tamangfood.data.model.auth

data class SignUpRequest (
    val fullName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val dateOfBirth: String,
)