package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.OtpRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OtpUseCase @Inject constructor(
    private val otpRepository: OtpRepository
) {
    suspend fun execute(email: String): Flow<NetworkState> {
        return otpRepository.requestOtp(email)
    }
}