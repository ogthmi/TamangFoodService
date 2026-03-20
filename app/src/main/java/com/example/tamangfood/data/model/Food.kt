package com.example.tamangfood.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.example.tamangfood.presentation.utils.FoodType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Food(
    val id: Int,
    val name: String,
    val price: String,
    val quantity: Int,
    val rating: Double,
    val type: FoodType,
    val description: String? = null,
    @DrawableRes val imageRes: Int
) : Parcelable
