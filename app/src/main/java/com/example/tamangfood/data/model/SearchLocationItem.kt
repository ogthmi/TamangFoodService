package com.example.tamangfood.data.model

import android.location.Address
import org.osmdroid.util.GeoPoint

data class SearchLocationItem(
    val address: Address,
    val geoPoint: GeoPoint,
    val fullAddress: String
)