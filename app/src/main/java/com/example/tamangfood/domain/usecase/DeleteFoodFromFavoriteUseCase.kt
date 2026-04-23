package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.FoodRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteFoodFromFavoriteUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend fun execute(foodId: Int): Flow<NetworkState> {
        return foodRepository.deleteFromFavorite(foodId)
    }
}
