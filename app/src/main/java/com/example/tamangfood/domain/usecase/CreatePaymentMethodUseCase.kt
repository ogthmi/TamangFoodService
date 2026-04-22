package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.PaymentRepository
import com.example.tamangfood.presentation.utils.NetworkState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreatePaymentMethodUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend fun execute(paymentMethodId: String): Flow<NetworkState> =
        paymentRepository.createPaymentMethod(paymentMethodId)
}
