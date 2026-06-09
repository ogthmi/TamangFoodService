package com.example.tamangfood.presentation.ui.mainapp.home.cart

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.tamangfood.domain.usecase.DeleteCartItemUseCase
import com.example.tamangfood.domain.usecase.GetCartItemsUseCase
import com.example.tamangfood.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val deleteCartItemUseCase: DeleteCartItemUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase
) : ViewModel() {
    private val _cartItemsState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val cartItemsState = _cartItemsState.asStateFlow()
    private val _deleteCartItemState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val deleteCartItemState = _deleteCartItemState.asStateFlow()
    private val _updateCartItemQuantityState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val updateCartItemQuantityState = _updateCartItemQuantityState.asStateFlow()

    fun getCartItems() {
        viewModelScope.launch {
            getCartItemsUseCase.execute().collect { state ->
                _cartItemsState.value = state
            }
        }
    }

    fun deleteCartItem(cartId: Int) {
        viewModelScope.launch {
            deleteCartItemUseCase.execute(cartId).collect { state ->
                _deleteCartItemState.value = state
            }
        }
    }

    fun resetDeleteCartItemState() {
        _deleteCartItemState.value = NetworkState.Init
    }

    fun updateCartItemQuantity(cartItemId: Int, quantity: Int) {
        viewModelScope.launch {
            updateCartItemQuantityUseCase.execute(cartItemId, quantity).collect { state ->
                _updateCartItemQuantityState.value = state
            }
        }
    }

    fun resetUpdateCartItemQuantityState() {
        _updateCartItemQuantityState.value = NetworkState.Init
    }
}