package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.FoodRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LeaveCommentUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend fun execute(
        orderId: Int,
        foodId: Int,
        rating: Double,
        comment: String
    ): Flow<NetworkState> {
        return foodRepository.leaveComment(orderId, foodId, rating, comment)
    }
}
