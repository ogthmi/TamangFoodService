package com.example.tamangfood.data.model.recommend

import com.example.tamangfood.data.model.food.ingredient.IngredientItem
import com.example.tamangfood.data.model.food.ingredient.toDomain
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.model.Ingredient

data class FoodsRecommendResponse(
    val code: Int,
    val message: String,
    val result: List<FoodRecommendItem>? = null
)

data class FoodRecommendItem(
    val id: Int,
    val name: String,
    val urlImage: String? = null,
    val description: String? = null,
    val price: Int,
    val avgRating: Double,
    val totalComment: Int,
    val totalLikes: Int,
    val hasLiked: Boolean,
    val totalBought: Int = 0,
    val quantity: Int = 0,
    val ingredientResponse: List<IngredientItem>? = null
)

fun FoodRecommendItem.toDomain(): Food = Food(
    id = id,
    name = name,
    urlImage = urlImage,
    description = description,
    price = price,
    avgRating = avgRating,
    totalComment = totalComment,
    totalLikes = totalLikes,
    hasLiked = hasLiked,
    quantity = quantity,
    ingredientResponse = ingredientResponse?.firstOrNull()?.toDomain()
        ?: Ingredient(id = 0, name = "", price = 0)
)
