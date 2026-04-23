package com.example.tamangfood.domain.repository

import com.example.tamangfood.domain.model.CartItem
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun createOrder(
        addressId: Int,
        deliveryPrice: Long,
        cartItems: List<CartItem>
    ): Flow<NetworkState>
}

