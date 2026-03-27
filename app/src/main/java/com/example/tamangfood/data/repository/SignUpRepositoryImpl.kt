package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.data.model.auth.signin.SignInRequest
import com.example.tamangfood.data.model.auth.signup.SignUpRequest
import com.example.tamangfood.domain.repository.SignUpRepository
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.sql.Timestamp
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): SignUpRepository{
    override suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        dateOfBirth: Long
    ): Flow<NetworkState> = callbackFlow{
        trySend(NetworkState.Loading)
        val response = apiService.signUp(SignUpRequest(email = email, password = password, fullName = fullName, phoneNumber = phoneNumber, dateOfBirth = dateOfBirth))
        if (response.isSuccessful) {
            val body = response.body()
            if (body?.code == HTTP.SUCCESS.status) {
                AppPreferences.saveToken(body.result.token.accessToken)
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
        awaitClose {  }
    }


}