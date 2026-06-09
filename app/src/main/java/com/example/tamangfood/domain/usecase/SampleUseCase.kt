package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.SampleRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SampleUseCase @Inject constructor(
    private val sampleRepository: SampleRepository
) {
    suspend fun execute(): Flow<NetworkState> {
        return sampleRepository.sample()
    }

}