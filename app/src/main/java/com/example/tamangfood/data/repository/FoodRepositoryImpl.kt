package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.data.model.food.FoodPageResult
import com.example.tamangfood.data.model.food.toDomain
import com.example.tamangfood.domain.model.FoodComment
import com.example.tamangfood.domain.repository.FoodRepository
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.IOException
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : FoodRepository {

    override suspend fun getFoodsByCategory(
        categoryId: Int,
        page: Int,
        size: Int
    ): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val result = fetchFoodsPage(categoryId, page, size)
            trySend(NetworkState.Success(result))
        } catch (e: Exception) {
            trySend(NetworkState.Error(e.message ?: "Error"))
        }
        awaitClose { }
    }

    override suspend fun fetchFoodsPage(
        categoryId: Int,
        page: Int,
        size: Int
    ): FoodPageResult {
        val response = apiService.getFoodsByCategory(categoryId, page, size)
        if (!response.isSuccessful) {
            val raw = response.errorBody()?.string()
            val err = runCatching { Gson().fromJson(raw, FailedResponse::class.java) }.getOrNull()
            throw IOException(err?.message ?: "HTTP Error")
        }
        val body = response.body() ?: throw IOException("Empty body")
        if (body.code != HTTP.SUCCESS.status || body.result == null) {
            throw IOException(body.message)
        }
        val pageDto = body.result
        val items = pageDto.content.map { it.toDomain() }
        return FoodPageResult(
            items = items,
            isLastPage = pageDto.last,
            pageNumber = pageDto.number
        )
    }

    override suspend fun getFoodDetail(foodId: Int): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.getFoodDetail(foodId)
            if (!response.isSuccessful) {
                val raw = response.errorBody()?.string()
                val err = runCatching { Gson().fromJson(raw, FailedResponse::class.java) }.getOrNull()
                throw IOException(err?.message ?: "HTTP Error")
            }
            val body = response.body() ?: throw IOException("Empty body")
            if (body.code != HTTP.SUCCESS.status || body.result == null) {
                throw IOException(body.message)
            }
            trySend(NetworkState.Success(body.result.toDomain()))
        } catch (e: Exception) {
            trySend(NetworkState.Error(e.message ?: "Error"))
        }
        awaitClose { }
    }

    override suspend fun fetchFoodComments(
        foodId: Int,
        page: Int,
        size: Int
    ): List<FoodComment> {
        val response = apiService.getCommentsByFood(foodId, page, size)
        if (!response.isSuccessful) {
            val raw = response.errorBody()?.string()
            val err = runCatching { Gson().fromJson(raw, FailedResponse::class.java) }.getOrNull()
            throw IOException(err?.message ?: "HTTP Error")
        }
        val body = response.body() ?: throw IOException("Empty body")
        if (body.code != HTTP.SUCCESS.status || body.result == null) {
            throw IOException(body.message)
        }
        return body.result.content.map { it.toDomain() }
    }
}
