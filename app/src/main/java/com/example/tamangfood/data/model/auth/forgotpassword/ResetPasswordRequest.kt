package com.example.tamangfood.data.model.auth.forgotpassword

data class ResetPasswordRequest(
    val otp: String,
    val email: String,
    val newPassword: String,
    val confirmNewPassword: String
)
