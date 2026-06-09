package com.example.tamangfood.domain.repository

import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    suspend fun getUserAddresses(): Flow<NetworkState>
    suspend fun getAddressById(addressId: Int): Flow<NetworkState>
    suspend fun addAddress(address: String, latitude: Double, longitude: Double): Flow<NetworkState>
    suspend fun updateAddress(
        addressId: Int,
        address: String,
        latitude: Double,
        longitude: Double
    ): Flow<NetworkState>
    suspend fun deleteAddress(addressId: Int): Flow<NetworkState>
}
