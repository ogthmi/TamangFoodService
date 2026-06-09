package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.PaymentRepository
import com.example.tamangfood.presentation.utils.NetworkState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreatePaymentIntentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend fun execute(orderId: Int, userId: Int, paymentMethodId: String): Flow<NetworkState> =
        paymentRepository.createPaymentIntent(orderId, userId, paymentMethodId)
}

