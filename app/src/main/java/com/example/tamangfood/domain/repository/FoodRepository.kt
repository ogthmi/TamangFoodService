package com.example.tamangfood.domain.repository

import com.example.tamangfood.data.model.food.FoodPageResult
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.model.FoodComment
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    suspend fun getFoodsByCategory(
        categoryId: Int,
        page: Int,
        size: Int
    ): Flow<NetworkState>

    suspend fun fetchFoodsPage(
        categoryId: Int,
        page: Int,
        size: Int
    ): FoodPageResult

    suspend fun getFoodDetail(foodId: Int): Flow<NetworkState>

    suspend fun fetchFoodComments(
        foodId: Int,
        page: Int = 0,
        size: Int = 20
    ): List<FoodComment>

    suspend fun fetchRecommendedFoods(): List<Food>

    suspend fun fetchBestSellerFoods(): List<Food>

    suspend fun addToFavorite(foodId: Int): Flow<NetworkState>

    suspend fun deleteFromFavorite(foodId: Int): Flow<NetworkState>

    suspend fun fetchFavoriteFoods(page: Int, size: Int): FoodPageResult

    suspend fun fetchFilteredFoods(
        categoryDetailIds: List<Long>,
        rating: Int,
        page: Int,
        size: Int
    ): FoodPageResult

    suspend fun leaveComment(
        orderId: Int,
        foodId: Int,
        rating: Double,
        comment: String
    ): Flow<NetworkState>
}
