package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.SignInRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val signInRepository: SignInRepository
) {
    suspend fun execute(email: String, password: String): Flow<NetworkState>{
        return signInRepository.signIn(email, password)
    }
}