package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.OrderRepository
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.OrderStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrdersByStatusUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend fun execute(status: OrderStatus, userId: Int): Flow<NetworkState> =
        orderRepository.getOrdersByStatus(status, userId)
}
