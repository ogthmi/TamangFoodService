package com.example.tamangfood.presentation.ui.mainapp.home

import androidx.annotation.DrawableRes

// Mockup data
data class FoodItem(
    val id: Int,
    val name: String,
    val price: String,
    @DrawableRes val imageRes: Int
)


