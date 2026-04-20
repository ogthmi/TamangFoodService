package com.example.tamangfood.domain.usecase

import com.example.tamangfood.domain.repository.CartRepository
import com.example.tamangfood.presentation.utils.NetworkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateCartItemQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend fun execute(cartItemId: Int, quantity: Int): Flow<NetworkState> =
        cartRepository.updateCartItemQuantity(cartItemId, quantity)
}
