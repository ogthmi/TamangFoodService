package com.example.tamangfood.data.model.order

data class CreateOrderRequest(
    val addressId: Long,
    val orderDetails: List<OrderDetailRequest>,
    val deliveryPrice: Long
)

data class OrderDetailRequest(
    val foodId: Long,
    val quantity: Int,
    val orderDetailIngredients: List<OrderDetailIngredientRequest> = emptyList()
)

data class OrderDetailIngredientRequest(
    val ingredientId: Long
)

data class CreateOrderResponse(
    val code: Int,
    val message: String,
    val result: OrderResult?
)

data class OrderResult(
    val id: Int
)

