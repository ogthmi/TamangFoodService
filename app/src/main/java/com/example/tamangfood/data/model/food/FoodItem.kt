package com.example.tamangfood.data.model.food

import com.example.tamangfood.data.model.food.ingredient.IngredientItem
import com.example.tamangfood.data.model.food.ingredient.toDomain
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.model.Ingredient

data class FoodItem(
    val id: Int,
    val name: String,
    val urlImage: String? = null,
    val description: String? = null,
    val price: Int,
    val avgRating: Double,
    val totalComment: Int,
    val totalLikes: Int,
    val hasLiked: Boolean,
    val quantity: Int,
    val ingredientResponse: IngredientItem? = null
)

fun FoodItem.toDomain() = Food(
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
    ingredientResponse = ingredientResponse?.toDomain() ?: Ingredient(id = 0, name = "", price = 0)
)