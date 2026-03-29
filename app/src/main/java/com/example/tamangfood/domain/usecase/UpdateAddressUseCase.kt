package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.AddressRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    suspend fun execute(
        addressId: Int,
        address: String,
        latitude: Double,
        longitude: Double
    ): Flow<NetworkState> = addressRepository.updateAddress(
        addressId = addressId,
        address = address,
        latitude = latitude,
        longitude = longitude
    )
}

