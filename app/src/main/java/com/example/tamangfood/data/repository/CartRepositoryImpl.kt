package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.data.model.cart.AddCartIngredientRequest
import com.example.tamangfood.data.model.cart.AddCartItemRequest
import com.example.tamangfood.data.model.cart.toDomain
import com.example.tamangfood.data.model.cart.UpdateCartItemRequest
import com.example.tamangfood.domain.repository.CartRepository
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : CartRepository {
    override suspend fun getCartItems(): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.getCartItems()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    val items = body.result.map { it.toDomain() }
                    trySend(NetworkState.Success(items))
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

    override suspend fun addCartItem(
        foodId: Int,
        quantity: Int,
        ingredientIds: List<Int>
    ): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.addCartItem(
                AddCartItemRequest(
                    foodId = foodId,
                    quantity = quantity,
                    ingredients = ingredientIds.map { AddCartIngredientRequest(it) }
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    trySend(NetworkState.Success(body.toDomain()))
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

    override suspend fun deleteCartItem(cartId: Int): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.deleteCartItem(cartId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    trySend(NetworkState.Success(body.toDomain()))
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

    override suspend fun updateCartItemQuantity(
        cartItemId: Int,
        quantity: Int
    ): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.updateCartItem(
                UpdateCartItemRequest(
                    cartItemId = cartItemId,
                    quantity = quantity
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    trySend(NetworkState.Success(body.toDomain()))
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
