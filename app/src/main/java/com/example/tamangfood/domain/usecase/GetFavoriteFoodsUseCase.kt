package com.example.tamangfood.domain.usecase

import com.example.tamangfood.data.model.food.FoodPageResult
import com.example.tamangfood.domain.repository.FoodRepository
import javax.inject.Inject

class GetFavoriteFoodsUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend fun execute(page: Int, size: Int): FoodPageResult {
        return foodRepository.fetchFavoriteFoods(page, size)
    }
}