package com.example.tamangfood.data.model.payment

data class Card(
    val paymentMethodId: String,
    val brand: String,
    val last4: String,
    val expMonth: Int,
    val expYear: Int,
    val createdAtMillis: Long = System.currentTimeMillis()
)

