package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.OrderRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CancelOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend fun execute(orderId: Int): Flow<NetworkState> {
        return orderRepository.cancelOrder(orderId)
    }
}
