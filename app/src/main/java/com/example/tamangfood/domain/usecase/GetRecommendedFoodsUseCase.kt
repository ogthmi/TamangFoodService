package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.repository.FoodRepository
import javax.inject.Inject

class GetRecommendedFoodsUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend fun execute(): List<Food> =
        foodRepository.fetchRecommendedFoods()
}
