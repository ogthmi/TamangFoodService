package com.example.tamangfood.domain.model

data class Card(
    val paymentMethodId: String,
    val brand: String,
    val last4: String,
    val expMonth: String,
    val expYear: String
)