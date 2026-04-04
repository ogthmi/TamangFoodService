package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.CategoryRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend fun execute(): Flow<NetworkState> = categoryRepository.getCategories()
}
