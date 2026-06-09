package com.example.tamangfood.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val id: Int,
    val food: Food,
    var quantity: Int,
    val dateTime: String // Format: "29/11/24 15:00"
): Parcelable

