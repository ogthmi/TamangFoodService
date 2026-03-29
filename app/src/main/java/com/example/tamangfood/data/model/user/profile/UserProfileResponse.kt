package com.example.tamangfood.data.model.user.profile

import java.util.Date

data class UserProfileResponse(
    val code: Int,
    val message: String,
    val result: UpdateProfileResultBody
)

data class UpdateProfileResultBody (
    val id: Int,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val dateOfBirth: String,
    val imageUrl: String
)