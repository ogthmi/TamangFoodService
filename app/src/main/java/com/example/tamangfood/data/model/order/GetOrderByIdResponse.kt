package com.example.tamangfood.data.model.order

data class GetOrderByIdResponse(
    val code: Int,
    val message: String,
    val result: OrderItemResponse? = null
)
