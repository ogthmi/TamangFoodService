package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.data.model.auth.signin.SignInRequest
import com.example.tamangfood.domain.repository.UserRepository
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {

    override suspend fun deleteAccount(userId: Int): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.deleteUser(userId)
            if (response.isSuccessful) {
                trySend(NetworkState.Success(null))
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

    override suspend fun getUserProfile(userId: Int): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)

        val response = apiService.getUser(userId)

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.code == HTTP.SUCCESS.status) {
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

    override suspend fun updateMyProfile(
        fullName: String, phoneNumber: String, dateOfBirth: Long, imageFile: File?
    ): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)

        try {
            val userId = AppPreferences.getUserId() ?: -1

            val fullNameBody = fullName.toByteArray().toRequestBody("text/plain".toMediaType())
            val phoneBody = phoneNumber.toByteArray().toRequestBody("text/plain".toMediaType())

            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val dateString = sdf.format(Date(dateOfBirth))
            val dateBody = dateString.toByteArray().toRequestBody("text/plain".toMediaType())

            val imagePart: MultipartBody.Part? = imageFile?.let {
                val reqFile = it.asRequestBody("image/*".toMediaType())
                MultipartBody.Part.createFormData("image", it.name, reqFile)
            }

            val response = apiService.updateUserProfile(
                userId = userId,
                fullName = fullNameBody,
                phoneNumber = phoneBody,
                dateOfBirth = dateBody,
                image = imagePart
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    trySend(NetworkState.Success(body))
                } else {
                    trySend(NetworkState.Error(body?.message ?: "Error"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, FailedResponse::class.java)
                trySend(NetworkState.Error(errorResponse?.message ?: "HTTP Error"))
            }
        } catch (e: Exception) {
            trySend(NetworkState.Error("Exception: ${e.localizedMessage}"))
        }

        awaitClose { }
    }
}
