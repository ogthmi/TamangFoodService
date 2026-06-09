package com.example.tamangfood.domain.repository

import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface ResetPasswordRepository {
    suspend fun resetPassword(
        otp: String,
        email: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<NetworkState>
}