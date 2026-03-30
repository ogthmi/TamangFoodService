package com.example.tamangfood.domain.model

data class Address(
    val id: Int,
    val name: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double,
)