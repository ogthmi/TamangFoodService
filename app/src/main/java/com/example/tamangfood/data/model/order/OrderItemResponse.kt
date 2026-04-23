package com.example.tamangfood.data.model.order

data class OrderItemResponse(
    val id: Int = 0,
    val status: String = "",
    val totalPrice: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val userId: Int = 0,
    val createdAt: String? = null,
    val deliveryPrice: Int = 0,
    val orderDetails: List<OrderDetailItemResponse> = emptyList()
)

data class OrderDetailItemResponse(
    val id: Int = 0,
    val food: OrderFoodItemResponse? = null,
    val ingredients: List<OrderIngredientItemResponse> = emptyList(),
    val quantity: Int = 0,
    val price: Int = 0
)

data class OrderFoodItemResponse(
    val id: Int = 0,
    val name: String = "",
    val urlImage: String? = null,
    val description: String? = null,
    val quantity: Int = 0,
    val price: Int = 0,
    val avgRating: Double = 0.0,
    val totalComment: Int = 0,
    val totalLikes: Int = 0,
    val hasLiked: Boolean = false
)

data class OrderIngredientItemResponse(
    val id: Int = 0,
    val name: String = "",
    val price: Int = 0
)
