package com.example.tamangfood.data.model.cart

data class GetCartItemsResponse(
    val code: Int,
    val message: String,
    val result: List<CartItemResult> = emptyList()
)
