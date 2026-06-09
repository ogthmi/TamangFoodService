package com.example.tamangfood.data.repository

import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.data.model.address.AddAddressRequest
import com.example.tamangfood.data.model.address.toDomain
import com.example.tamangfood.domain.repository.AddressRepository
import com.example.tamangfood.presentation.utils.HTTP
import com.example.tamangfood.presentation.utils.NetworkState
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AddressRepository {

    override suspend fun getUserAddresses(): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.getUserAddresses()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    val list = body.result?.map { it.toDomain() }.orEmpty()
                    trySend(NetworkState.Success(list))
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

    override suspend fun getAddressById(addressId: Int): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.getAddressById(addressId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    val item = body.result
                    if (item != null) {
                        trySend(NetworkState.Success(item.toDomain()))
                    } else {
                        trySend(NetworkState.Error(body.message))
                    }
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

    override suspend fun addAddress(
        address: String,
        latitude: Double,
        longitude: Double
    ): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.addAddress(
                AddAddressRequest(address = address, latitude = latitude, longitude = longitude)
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    trySend(NetworkState.Success(body))
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

    override suspend fun updateAddress(
        addressId: Int,
        address: String,
        latitude: Double,
        longitude: Double
    ): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.updateAddress(
                addressId = addressId,
                body = AddAddressRequest(
                    address = address,
                    latitude = latitude,
                    longitude = longitude
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    trySend(NetworkState.Success(body))
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

    override suspend fun deleteAddress(addressId: Int): Flow<NetworkState> = callbackFlow {
        trySend(NetworkState.Loading)
        try {
            val response = apiService.deleteAddress(addressId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == HTTP.SUCCESS.status) {
                    trySend(NetworkState.Success(body))
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
