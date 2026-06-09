package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.UserRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.Date
import javax.inject.Inject

class UpdateMyProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun execute(
        fullName: String,
        phoneNumber: String,
        dateOfBirth: Long,
        imageFile: File?
    ): Flow<NetworkState> {
        return userRepository.updateMyProfile(
            fullName,
            phoneNumber,
            dateOfBirth,
            imageFile
        )
    }
}