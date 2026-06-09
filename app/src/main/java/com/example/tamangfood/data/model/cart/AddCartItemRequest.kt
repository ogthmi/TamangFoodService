package com.example.tamangfood.data.model.cart

data class AddCartItemRequest(
    val foodId: Int,
    val quantity: Int,
    val ingredients: List<AddCartIngredientRequest> = emptyList()
)

data class AddCartIngredientRequest(
    val ingredientId: Int
)
