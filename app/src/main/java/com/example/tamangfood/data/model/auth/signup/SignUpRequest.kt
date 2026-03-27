package com.example.tamangfood.data.model.auth.signup

import java.sql.Timestamp

data class SignUpRequest (
    val fullName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val dateOfBirth: Long,
)