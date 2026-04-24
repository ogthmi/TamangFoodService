package com.example.tamangfood.data.model.food

data class FilterFoodRequest(
    val categoryDetailIds: List<Long>,
    val rating: Int
)
