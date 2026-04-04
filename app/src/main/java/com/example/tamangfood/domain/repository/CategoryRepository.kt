package com.example.tamangfood.domain.repository

import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategories(): Flow<NetworkState>
    suspend fun getCategoryDetails(categoryId: Int): Flow<NetworkState>
}
