package com.example.tamangfood.domain.model

data class CartItem(
    val id: Int,
    val userId: Int,
    val food: CartFood,
    val ingredients: List<Ingredient> = emptyList(),
    val quantity: Int
)

data class CartSummary(
    val userId: Int,
    val totalPrice: Int,
    val carts: List<CartItem> = emptyList()
)

data class CartFood(
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
    val ingredientResponse: List<Ingredient> = emptyList(),
    val quantity: Int,
)
