package com.example.tamangfood.domain.repository

import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun getCartItems(): Flow<NetworkState>

    suspend fun addCartItem(
        foodId: Int,
        quantity: Int,
        ingredientIds: List<Int> = emptyList()
    ): Flow<NetworkState>

    suspend fun deleteCartItem(cartId: Int): Flow<NetworkState>

    suspend fun updateCartItemQuantity(
        cartItemId: Int,
        quantity: Int
    ): Flow<NetworkState>
}
