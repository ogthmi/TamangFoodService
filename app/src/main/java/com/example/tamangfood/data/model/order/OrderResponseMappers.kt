package com.example.tamangfood.data.model.order

import com.example.tamangfood.domain.model.Order
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.model.Ingredient
import com.example.tamangfood.presentation.utils.OrderStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun GetOrdersByStatusResponse.toDomain(): List<Order> =
    result?.content.orEmpty().map { it.toDomain() }

fun OrderItemResponse.toDomain(): Order {
    val items = orderDetails.map { it.toDomainFood() }
    return Order(
        id = id,
        name = "Order #$id",
        price = totalPrice,
        deliveryTax = deliveryPrice,
        dateTime = createdAt.toDisplayDateTime(),
        itemCount = items.sumOf { it.quantity },
        status = status.toDomainOrderStatus(),
        items = items
    )
}

private fun OrderDetailItemResponse.toDomainFood(): Food {
    val food = food ?: OrderFoodItemResponse()
    val ingredientDisplay = ingredients
        .filter { it.name.isNotBlank() }
        .joinToString(separator = "\n") { "${it.name}: $${it.price}" }
    return Food(
        id = food.id,
        name = food.name.ifBlank { "Food #${food.id}" },
        urlImage = food.urlImage,
        description = food.description,
        price = food.price,
        avgRating = food.avgRating,
        totalComment = food.totalComment,
        totalLikes = food.totalLikes,
        hasLiked = food.hasLiked,
        quantity = quantity,
        ingredientResponse = Ingredient(
            id = ingredients.firstOrNull()?.id ?: 0,
            name = ingredientDisplay,
            price = 0
        )
    )
}

private fun String.toDomainOrderStatus(): OrderStatus {
    return when (uppercase(Locale.US)) {
        "DELIVERED", "COMPLETED" -> OrderStatus.COMPLETED
        "CANCELLED", "CANCELED" -> OrderStatus.CANCELLED
        else -> OrderStatus.ACTIVE
    }
}

private fun String?.toDisplayDateTime(): String {
    if (this.isNullOrBlank()) return "-"
    return runCatching {
        LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a", Locale.US))
    }.getOrDefault(this)
}
