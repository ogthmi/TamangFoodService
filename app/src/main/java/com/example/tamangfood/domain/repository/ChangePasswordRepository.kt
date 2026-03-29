package com.example.tamangfood.domain.repository

import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface ChangePasswordRepository {
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<NetworkState>
}