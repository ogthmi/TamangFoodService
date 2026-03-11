package com.example.tamangfood.data.model

import androidx.annotation.DrawableRes

data class CartItem(
    val id: Int,
    val food: Food,
    var quantity: Int,
    val dateTime: String // Format: "29/11/24 15:00"
)

