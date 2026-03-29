package com.example.tamangfood.data.model.user.changepassword

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmNewPassword: String,
)
