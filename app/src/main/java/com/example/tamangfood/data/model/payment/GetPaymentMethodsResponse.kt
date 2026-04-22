package com.example.tamangfood.data.model.payment

data class GetPaymentMethodsResponse(
    val code: Int,
    val message: String,
    val result: List<PaymentMethod>?
)
