package com.example.tamangfood.data.model.cart

import com.example.tamangfood.domain.model.CartItem

data class UpdateCartItemResponse(
    val code: Int,
    val message: String,
    val result: CartItemResult? = null
)

fun UpdateCartItemResponse.toDomain(): CartItem? = result?.toDomain()
