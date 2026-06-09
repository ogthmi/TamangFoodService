package com.example.tamangfood.data.model.cart

import com.example.tamangfood.domain.model.CartSummary

data class GetCartItemsResponse(
    val code: Int,
    val message: String,
    val result: CartItemsResult? = null
)

data class CartItemsResult(
    val userId: Int,
    val totalPrice: Int,
    val carts: List<CartItemResult> = emptyList()
)

fun CartItemsResult.toDomain(): CartSummary =
    CartSummary(
        userId = userId,
        totalPrice = totalPrice,
        carts = carts.map { it.toDomain() }
    )
