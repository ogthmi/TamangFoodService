package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.ResetPasswordRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val resetPasswordRepository: ResetPasswordRepository
) {
    suspend fun execute(
        otp: String,
        email: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<NetworkState> {
        return resetPasswordRepository.resetPassword(otp, email, newPassword, confirmPassword)
    }
}