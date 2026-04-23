package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.data.model.order.CreateOrderRequest
import com.example.tamangfood.data.model.order.OrderDetailIngredientRequest
import com.example.tamangfood.data.model.order.OrderDetailRequest
import com.example.tamangfood.domain.model.CartItem
import com.example.tamangfood.domain.repository.OrderRepository
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.google.gson.Gson
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OrderRepository {
    override suspend fun createOrder(
        addressId: Int,
        deliveryPrice: Long,
        cartItems: List<CartItem>
    ): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.createOrder(
                CreateOrderRequest(
                    addressId = addressId.toLong(),
                    deliveryPrice = deliveryPrice,
                    orderDetails = cartItems.map { cartItem ->
                        OrderDetailRequest(
                            foodId = cartItem.food.id.toLong(),
                            quantity = cartItem.quantity,
                            orderDetailIngredients = cartItem.ingredients.map { ingredient ->
                                OrderDetailIngredientRequest(ingredientId = ingredient.id.toLong())
                            }
                        )
                    }
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status || body?.code == HTTP.CREATED.status) {
                    trySend(NetworkState.Success(body.result?.id))
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

