package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.model.FoodComment
import com.example.tamangfood.domain.repository.FoodRepository
import javax.inject.Inject

class GetFoodCommentsUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend fun execute(
        foodId: Int,
        page: Int = 0,
        size: Int = 20,
    ): List<FoodComment> =
        foodRepository.fetchFoodComments(foodId, page, size)
}
