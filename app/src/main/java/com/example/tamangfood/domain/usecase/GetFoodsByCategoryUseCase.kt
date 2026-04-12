package com.example.tamangfood.domain.usecase

import com.example.tamangfood.data.model.food.FoodPageResult
import com.example.tamangfood.domain.repository.FoodRepository
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoodsByCategoryUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend fun execute(
        categoryId: Int,
        page: Int,
        size: Int
    ): Flow<NetworkState> =
        foodRepository.getFoodsByCategory(categoryId, page, size)

    suspend fun fetchFoodsPage(
        categoryId: Int,
        page: Int,
        size: Int
    ): FoodPageResult =
        foodRepository.fetchFoodsPage(categoryId, page, size)
}
