package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.UserRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun execute(userId: Int): Flow<NetworkState> =
        userRepository.deleteAccount(userId)
}
