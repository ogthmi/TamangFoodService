package com.example.tamangfood.data.model.food.ingredient

import com.example.tamangfood.domain.model.Ingredient


data class IngredientItem(
    val id: Int,
    val name: String,
    val price: Int
)

fun IngredientItem.toDomain() = Ingredient(
    id = id,
    name = name,
    price = price
)