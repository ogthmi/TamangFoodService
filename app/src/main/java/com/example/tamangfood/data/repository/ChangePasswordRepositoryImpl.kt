package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.data.model.user.changepassword.ChangePasswordRequest
import com.example.tamangfood.domain.repository.ChangePasswordRepository
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ChangePasswordRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ChangePasswordRepository {

    override suspend fun changePassword(
        currentPassword: String, newPassword: String, confirmPassword: String
    ): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)

        val request = ChangePasswordRequest(currentPassword, newPassword, confirmPassword)

        val response = apiService.changePassword(request)

        if (response.isSuccessful) {
            val body = response.body()

            if (body?.code == HTTP.SUCCESS.status) trySend(NetworkState.Success(body.message))
            else trySend(NetworkState.Error(body?.message ?: "Error"))
        }

        else {
            val errorBody = response.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FailedResponse::class.java)

            trySend(NetworkState.Error(errorResponse?.message ?: "HTTP Error"))
        }

        awaitClose {  }
    }
}