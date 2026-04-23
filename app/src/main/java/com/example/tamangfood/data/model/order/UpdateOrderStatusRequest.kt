package com.example.tamangfood.data.model.order

data class UpdateOrderStatusRequest(
    val orderId: Int,
    val orderStatus: String
)
