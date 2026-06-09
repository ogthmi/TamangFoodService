package com.example.tamangfood.data.model.payment

data class CreatePaymentIntentResponse(
    val code: Int,
    val message: String,
    val result: String?
)

