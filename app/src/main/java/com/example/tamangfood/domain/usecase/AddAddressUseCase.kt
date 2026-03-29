package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.AddressRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    suspend fun execute(address: String, latitude: Double, longitude: Double): Flow<NetworkState> =
        addressRepository.addAddress(address, latitude, longitude)
}
