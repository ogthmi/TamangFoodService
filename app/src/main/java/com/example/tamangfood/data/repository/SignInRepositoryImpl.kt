package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.data.model.auth.signin.SignInRequest
import com.example.tamangfood.domain.repository.SignInRepository
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SignInRepository {

    override suspend fun signIn(
        email: String,
        password: String
    ): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        val response = apiService.signIn(SignInRequest(email = email, password = password))

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.code == HTTP.SUCCESS.status) {

                AppPreferences.saveToken(body.result.token.accessToken)
                AppPreferences.saveRefreshToken(body.result.token.refreshToken)
                AppPreferences.saveUserId(body.result.id)

                trySend(NetworkState.Success(body))
            } else {
                trySend(NetworkState.Error(body?.message ?: "Error"))
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FailedResponse::class.java)
            trySend(NetworkState.Error(errorResponse.message ?: "HTTP Error"))
        }
        awaitClose { }
    }
}
