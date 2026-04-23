package com.example.tamangfood.domain.repository

import com.example.tamangfood.domain.model.CartItem
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getOrdersByStatus(
        status: OrderStatus,
        userId: Int
    ): Flow<NetworkState>

    suspend fun createOrder(
        addressId: Int,
        deliveryPrice: Long,
        cartItems: List<CartItem>
    ): Flow<NetworkState>
}

