package com.example.tamangfood.domain.repository

import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun createPaymentMethod(paymentMethodId: String): Flow<NetworkState>
    suspend fun getPaymentMethods(): Flow<NetworkState>
    suspend fun deletePaymentMethod(paymentMethodId: String): Flow<NetworkState>
}
