package com.example.tamangfood.domain.repository

import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun deleteAccount(userId: Int): Flow<NetworkState>
}
