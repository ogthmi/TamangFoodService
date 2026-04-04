package com.example.tamangfood.domain.repository

import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface UserRepository {
    suspend fun deleteAccount(userId: Int): Flow<NetworkState>

    suspend fun getUserProfile(userId: Int): Flow<NetworkState>

    suspend fun updateMyProfile(
        fullName: String,
        phoneNumber: String,
        dateOfBirth: Long,
        imageFile: File?
    ): Flow<NetworkState>
}
