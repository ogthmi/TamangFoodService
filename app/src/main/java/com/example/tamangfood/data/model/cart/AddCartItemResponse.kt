package com.example.tamangfood.data.model.cart

import com.example.tamangfood.data.model.food.ingredient.IngredientItem
import com.example.tamangfood.data.model.food.ingredient.toDomain
import com.example.tamangfood.domain.model.CartFood
import com.example.tamangfood.domain.model.CartItem

data class AddCartItemResponse(
    val code: Int,
    val message: String,
    val result: CartItemResult? = null
)

data class CartItemResult(
    val id: Int,
    val userId: Int,
    val food: CartFoodItem,
    val ingredients: List<IngredientItem> = emptyList(),
    val quantity: Int
)

data class CartFoodItem(
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
    val quantity: Int,
    val ingredientResponse: List<IngredientItem>? = null
)

fun AddCartItemResponse.toDomain(): CartItem? = result?.toDomain()

fun CartItemResult.toDomain(): CartItem =
    CartItem(
        id = id,
        userId = userId,
        food = food.toDomain(),
        ingredients = ingredients.map { it.toDomain() },
        quantity = quantity
    )

fun CartFoodItem.toDomain(): CartFood =
    CartFood(
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
        ingredientResponse = ingredientResponse.orEmpty().map { it.toDomain() },
        quantity = quantity
    )