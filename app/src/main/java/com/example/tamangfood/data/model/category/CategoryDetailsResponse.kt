package com.example.tamangfood.data.model.category

data class CategoryDetailsResponse(
    val code: Int,
    val message: String,
    val result: List<CategoryDetailItem>? = null
)

data class CategoryDetailItem(
    val id: Int,
    val name: String,
    val categoryId: Int
)
