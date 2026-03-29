package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.ChangePasswordRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val changePasswordRepository: ChangePasswordRepository
) {
    suspend fun execute(
        currentPassword: String, newPassword: String, confirmPassword: String
    ): Flow<NetworkState> {
        return changePasswordRepository.changePassword(
            currentPassword,
            newPassword,
            confirmPassword
        )
    }
}