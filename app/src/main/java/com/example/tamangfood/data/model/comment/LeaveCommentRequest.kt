package com.example.tamangfood.data.model.comment

data class LeaveCommentRequest (
    val orderId: Int,
    val foodId: Int,
    val rating: Double,
    val comment: String
)