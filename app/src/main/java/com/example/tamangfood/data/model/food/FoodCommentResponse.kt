package com.example.tamangfood.data.model.food

import com.example.tamangfood.domain.model.FoodComment
import com.google.gson.annotations.SerializedName

data class CommentsByFoodResponse(
    val code: Int,
    val message: String,
    val result: PageDto<FoodCommentItem>? = null
)

data class FoodCommentItem(
    @SerializedName(value = "userId", alternate = ["user_id"])
    val userId: Long = 0L,
    @SerializedName(value = "fullName", alternate = ["full_name"])
    val fullName: String? = null,
    @SerializedName(value = "urlImage", alternate = ["url_image"])
    val urlImage: String? = null,
    val comment: String? = null,
    val rating: Double = 0.0,
    @SerializedName(value = "createdAt", alternate = ["created_at"])
    val createdAt: String? = null,
    @SerializedName(value = "isMe", alternate = ["is_me"])
    val isMe: Boolean = false
)

fun FoodCommentItem.toDomain(): FoodComment {
    val stableId = "${userId}_${createdAt ?: comment?.hashCode() ?: 0}"
    return FoodComment(
        id = stableId,
        fullName = fullName.orEmpty(),
        rating = rating.coerceIn(0.0, 5.0),
        text = comment.orEmpty(),
        avatarUrl = urlImage
    )
}
