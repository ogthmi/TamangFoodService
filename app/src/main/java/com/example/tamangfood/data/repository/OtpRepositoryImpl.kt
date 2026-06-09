package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.domain.repository.OtpRepository
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class OtpRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OtpRepository {

    override suspend fun requestOtp(email: String): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)

        val response = apiService.requestOTP(email = email)

        if (response.isSuccessful) {
            val body = response.body()

            if (body?.code == HTTP.SUCCESS.status) {
                trySend(NetworkState.Success(body.message))
            } else {
                trySend(NetworkState.Error(body?.message ?: "Error"))
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FailedResponse::class.java)

            trySend(NetworkState.Error(errorResponse?.message ?: "HTTP Error"))
        }
        awaitClose { }
    }
}