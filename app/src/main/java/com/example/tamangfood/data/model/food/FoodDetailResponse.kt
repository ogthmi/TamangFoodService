package com.example.tamangfood.data.model.food

import com.example.tamangfood.data.model.food.ingredient.IngredientItem
import com.example.tamangfood.data.model.food.ingredient.toDomain
import com.example.tamangfood.domain.model.FoodDetail

data class FoodDetailResponse(
    val code: Int,
    val message: String,
    val result: FoodDetailItem? = null
)

data class FoodDetailItem(
    val id: Int,
    val name: String,
    val urlImage: String? = null,
    val description: String? = null,
    val price: Int,
    val avgRating: Double,
    val totalComment: Int,
    val totalLikes: Int,
    val hasLiked: Boolean,
    val totalBought: Int,
    val ingredientResponse: List<IngredientItem>? = null
)

fun FoodDetailItem.toDomain(): FoodDetail = FoodDetail(
    id = id,
    name = name,
    urlImage = urlImage,
    description = description,
    price = price,
    avgRating = avgRating,
    totalComment = totalComment,
    totalLikes = totalLikes,
    hasLiked = hasLiked,
    totalBought = totalBought,
    ingredients = ingredientResponse.orEmpty().map { it.toDomain() }
)
