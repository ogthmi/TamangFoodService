package com.example.tamangfood.data.model.address

import com.example.tamangfood.domain.model.Address

data class UserAddressesResponse(
    val code: Int,
    val message: String,
    val result: List<AddressItem>? = null
)

data class AddressItem(
    val id: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

fun AddressItem.toDomain(): Address = Address(
    id = id,
    name = address,
    fullAddress = "",
    latitude = latitude,
    longitude = longitude
)