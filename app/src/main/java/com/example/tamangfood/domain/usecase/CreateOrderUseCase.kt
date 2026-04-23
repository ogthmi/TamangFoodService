package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.model.CartItem
import com.example.tamangfood.domain.repository.OrderRepository
import com.example.tamangfood.presentation.utils.NetworkState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend fun execute(
        addressId: Int,
        deliveryPrice: Long,
        cartItems: List<CartItem>
    ): Flow<NetworkState> = orderRepository.createOrder(
        addressId = addressId,
        deliveryPrice = deliveryPrice,
        cartItems = cartItems
    )
}

