package com.example.tamangfood.domain.model

import java.sql.Timestamp

data class User(
    val id: Int = -1,
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String,
    val dateOfBirth: Timestamp
)