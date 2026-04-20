package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.CartRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend fun execute(
        foodId: Int,
        quantity: Int,
        ingredientIds: List<Int> = emptyList()
    ): Flow<NetworkState> = cartRepository.addCartItem(foodId, quantity, ingredientIds)
}
