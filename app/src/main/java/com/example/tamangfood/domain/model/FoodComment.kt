package com.example.tamangfood.domain.model

data class FoodComment(
    val id: String,
    val fullName: String,
    val rating: Double,
    val text: String,
    val avatarUrl: String? = null,
)
