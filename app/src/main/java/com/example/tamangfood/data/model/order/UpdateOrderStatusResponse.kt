package com.example.tamangfood.data.model.order

data class UpdateOrderStatusResponse(
    val code: Int,
    val message: String,
    val result: OrderItemResponse? = null
)
