package com.example.tamangfood.domain.repository

import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface OtpRepository {
    suspend fun requestOtp(email: String): Flow<NetworkState>
}