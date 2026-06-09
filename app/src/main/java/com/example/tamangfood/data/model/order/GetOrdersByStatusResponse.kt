package com.example.tamangfood.data.model.order

data class GetOrdersByStatusResponse(
    val code: Int,
    val message: String,
    val result: OrdersPageResult? = null
)

data class OrdersPageResult(
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val size: Int = 0,
    val content: List<OrderItemResponse> = emptyList()
)
