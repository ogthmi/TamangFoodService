package com.example.tamangfood.data.model.cart

data class UpdateCartItemRequest(
    val cartItemId: Int,
    val quantity: Int
)
