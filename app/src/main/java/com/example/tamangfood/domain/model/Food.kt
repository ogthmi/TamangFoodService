package com.example.tamangfood.domain.model

import android.os.Parcelable
import com.example.tamangfood.data.model.food.ingredient.IngredientItem
import com.example.tamangfood.presentation.utils.FoodType
import kotlinx.parcelize.Parcelize

data class Food(
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
    val ingredientResponse: Ingredient
)