package com.example.tamangfood.data.model.category

data class CategoryResponse(
    val code: Int,
    val message: String,
    val result: List<CategoryItem>? = null
)

data class CategoryItem(
    val id: Int,
    val name: String
)
