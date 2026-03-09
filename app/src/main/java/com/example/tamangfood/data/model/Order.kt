package com.example.tamangfood.data.model

import androidx.annotation.DrawableRes
import com.example.tamangfood.presentation.utils.OrderStatus

data class Order(
    val id: Int,
    val name: String,
    val price: String,
    val dateTime: String,
    val itemCount: Int,
    val status: OrderStatus,
    @DrawableRes val imageRes: Int,
    val items: List<Food> = emptyList(),
    val statusText: String = when (status) {
        OrderStatus.ACTIVE -> "Order placed"
        OrderStatus.COMPLETED -> "Order delivered"
        OrderStatus.CANCELLED -> "Order cancelled"
    }
)
