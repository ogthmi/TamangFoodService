package com.example.tamangfood.domain.usecase

import com.example.tamangfood.data.model.food.FoodPageResult
import com.example.tamangfood.domain.repository.FoodRepository
import javax.inject.Inject

class GetFilteredFoodsUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend fun execute(
        categoryDetailIds: List<Long>,
        rating: Int,
        page: Int,
        size: Int
    ): FoodPageResult {
        return foodRepository.fetchFilteredFoods(categoryDetailIds, rating, page, size)
    }
}
