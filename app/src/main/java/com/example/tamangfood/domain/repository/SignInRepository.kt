package com.example.tamangfood.domain.repository

import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface SignInRepository {
    suspend fun signIn(
        email: String,
        password: String
    ): Flow<NetworkState>
}