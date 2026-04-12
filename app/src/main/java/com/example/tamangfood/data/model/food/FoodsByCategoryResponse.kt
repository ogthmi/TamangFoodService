package com.example.tamangfood.data.model.food

import com.google.gson.annotations.SerializedName

data class FoodsByCategoryResponse(
    val code: Int,
    val message: String,
    val result: PageDto<FoodItem>? = null
)

data class PageDto<T>(
    val content: List<T> = emptyList(),
    val last: Boolean = true,
    val totalPages: Int = 0,
    val totalElements: Long = 0,
    val number: Int = 0,
    val size: Int = 0
)

