package com.example.tamangfood.domain.repository

import com.example.tamangfood.data.model.auth.signup.SignUpRequest
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp

interface SignUpRepository {
    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        dateOfBirth: Long)
    : Flow<NetworkState>
}