package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.domain.model.FoodCategory
import com.example.tamangfood.domain.model.FoodCategoryDetail
import com.example.tamangfood.domain.repository.CategoryRepository
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.toFoodTypeOrDefault
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : CategoryRepository {

    override suspend fun getCategories(): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    val list = body.result.orEmpty().map {
                        FoodCategory(id = it.id, name = it.name, type = it.name.toFoodTypeOrDefault())
                    }
                    trySend(NetworkState.Success(list))
                } else {
                    trySend(NetworkState.Error(body?.message ?: "Error"))
                }
            } else {
                val raw = response.errorBody()?.string()
                val err = runCatching { Gson().fromJson(raw, FailedResponse::class.java) }.getOrNull()
                trySend(NetworkState.Error(err?.message ?: "HTTP Error"))
            }
        } catch (e: Exception) {
            trySend(NetworkState.Error(e.message ?: "Error"))
        }
        awaitClose { }
    }

    override suspend fun getCategoryDetails(categoryId: Int): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.getCategoryDetails(categoryId)
            if (response.isSuccessful) {
                val body = response.body()
                val items = body?.result
                if (items != null) {
                    val list = items.map {
                        FoodCategoryDetail(
                            id = it.id,
                            name = it.name,
                            categoryId = it.categoryId
                        )
                    }
                    trySend(NetworkState.Success(list))
                } else {
                    trySend(NetworkState.Error(body?.message ?: "Error"))
                }
            } else {
                val raw = response.errorBody()?.string()
                val err = runCatching { Gson().fromJson(raw, FailedResponse::class.java) }.getOrNull()
                trySend(NetworkState.Error(err?.message ?: "HTTP Error"))
            }
        } catch (e: Exception) {
            trySend(NetworkState.Error(e.message ?: "Error"))
        }
        awaitClose { }
    }
}
