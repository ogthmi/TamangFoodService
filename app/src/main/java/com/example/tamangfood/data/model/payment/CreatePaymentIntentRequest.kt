package com.example.tamangfood.data.model.payment

data class CreatePaymentIntentRequest(
    val orderId: Int,
    val userId: Int,
    val paymentMethodId: String
)

