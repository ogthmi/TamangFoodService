package com.example.tamangfood.domain.model

data class FoodDetail(
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
    val ingredients: List<Ingredient> = emptyList(),
    val quantity: Int,
)
