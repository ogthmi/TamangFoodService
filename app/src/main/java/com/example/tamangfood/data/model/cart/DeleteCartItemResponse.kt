package com.example.tamangfood.data.model.cart

import com.example.tamangfood.domain.model.CartItem

data class DeleteCartItemResponse(
    val code: Int,
    val message: String,
    val result: CartItemResult? = null
)

fun DeleteCartItemResponse.toDomain(): CartItem? = result?.toDomain()
