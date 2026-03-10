package com.example.tamangfood.data.model

import androidx.annotation.DrawableRes
import com.example.tamangfood.presentation.utils.FoodType

data class Food(
    val id: Int,
    val name: String,
    val price: String,
    val quantity: Int,
    val rating: Double,
    val type: FoodType,
    @DrawableRes val imageRes: Int
)
