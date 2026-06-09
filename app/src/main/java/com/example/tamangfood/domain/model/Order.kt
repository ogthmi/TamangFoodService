package com.example.tamangfood.domain.model

import androidx.annotation.DrawableRes
import com.example.tamangfood.presentation.utils.OrderStatus

data class Order(
    val id: Int,
    val name: String,
    val price: Int,
    val deliveryTax: Int = 0,
    val dateTime: String,
    val itemCount: Int,
    val status: OrderStatus,
    val items: List<Food> = emptyList(),
    val statusText: String = when (status) {
        OrderStatus.ACTIVE -> "Order placed"
        OrderStatus.COMPLETED -> "Order delivered"
        OrderStatus.CANCELLED -> "Order cancelled"
    }
)