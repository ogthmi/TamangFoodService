package com.example.tamangfood.data.model.food

import com.example.tamangfood.domain.model.Food

data class FoodPageResult(
    val items: List<Food>,
    val isLastPage: Boolean,
    val pageNumber: Int
)
