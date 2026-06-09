package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.data.model.payment.CreatePaymentIntentRequest
import com.example.tamangfood.data.model.payment.CreatePaymentMethodRequest
import com.example.tamangfood.data.model.payment.toDomain
import com.example.tamangfood.domain.repository.PaymentRepository
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.google.gson.Gson
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PaymentRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : PaymentRepository {
    override suspend fun createPaymentMethod(paymentMethodId: String): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.createPaymentMethod(
                CreatePaymentMethodRequest(paymentMethodId = paymentMethodId)
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status || body?.code == HTTP.CREATED.status) {
                    trySend(NetworkState.Success(body.result))
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

    override suspend fun getPaymentMethods(): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.getPaymentMethods()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    val cards = body.result
                        ?.map { it.toDomain() }
                        ?.filter { it.paymentMethodId.isNotBlank() }
                        .orEmpty()
                    trySend(NetworkState.Success(cards))
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

    override suspend fun deletePaymentMethod(paymentMethodId: String): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.deletePaymentMethod(paymentMethodId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    trySend(NetworkState.Success(body.result?.toDomain()))
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

    override suspend fun createPaymentIntent(
        orderId: Int,
        userId: Int,
        paymentMethodId: String
    ): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.createPaymentIntent(
                CreatePaymentIntentRequest(
                    orderId = orderId,
                    userId = userId,
                    paymentMethodId = paymentMethodId
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status || body?.code == HTTP.CREATED.status) {
                    trySend(NetworkState.Success(body.result))
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
