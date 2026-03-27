package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.SignUpRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val signUpRepository: SignUpRepository
){
    suspend fun execute(email: String, password: String, fullName: String, phoneNumber: String, dateOfBirth: Long): Flow<NetworkState>{
        return signUpRepository.signUp(email, password, fullName, phoneNumber, dateOfBirth)
    }
}