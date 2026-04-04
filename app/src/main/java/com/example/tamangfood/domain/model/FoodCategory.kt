package com.example.tamangfood.domain.model

import com.example.tamangfood.presentation.utils.FoodType

data class FoodCategory(
    val id: Int,
    val name: String,
    val type: FoodType
)
