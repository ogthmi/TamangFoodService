package com.example.tamangfood.presentation.ui.mainapp.home.cart.confirmorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.GetCartItemsUseCase
import com.example.tamangfood.domain.model.CartItem
import com.example.tamangfood.domain.usecase.CreateOrderUseCase
import com.example.tamangfood.domain.usecase.CreatePaymentIntentUseCase
import com.example.tamangfood.domain.usecase.GetPaymentMethodsUseCase
import com.example.tamangfood.domain.usecase.GetUserAddressUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ConfirmOrderViewModel @Inject constructor(
    private val getUserAddressUseCase: GetUserAddressUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase,
    private val createPaymentIntentUseCase: CreatePaymentIntentUseCase,
    private val createOrderUseCase: CreateOrderUseCase
) : ViewModel() {
    private val _addressesState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val addressesState = _addressesState.asStateFlow()
    private val _cartItemsState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val cartItemsState = _cartItemsState.asStateFlow()
    private val _paymentMethodsState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val paymentMethodsState = _paymentMethodsState.asStateFlow()
    private val _createPaymentIntentState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val createPaymentIntentState = _createPaymentIntentState.asStateFlow()
    private val _createOrderState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val createOrderState = _createOrderState.asStateFlow()

    fun loadAddresses() {
        if (_addressesState.value is NetworkState.Loading) return
        viewModelScope.launch {
            getUserAddressUseCase.execute().collect { state ->
                _addressesState.value = state
            }
        }
    }

    fun loadCartItems() {
        if (_cartItemsState.value is NetworkState.Loading) return
        viewModelScope.launch {
            getCartItemsUseCase.execute().collect { state ->
                _cartItemsState.value = state
            }
        }
    }

    fun loadPaymentMethods() {
        if (_paymentMethodsState.value is NetworkState.Loading) return
        viewModelScope.launch {
            getPaymentMethodsUseCase.execute().collect { state ->
                _paymentMethodsState.value = state
            }
        }
    }

    fun createPaymentIntent(orderId: Int, userId: Int, paymentMethodId: String) {
        if (_createPaymentIntentState.value is NetworkState.Loading) return
        viewModelScope.launch {
            createPaymentIntentUseCase.execute(orderId, userId, paymentMethodId).collect { state ->
                _createPaymentIntentState.value = state
            }
        }
    }

    fun resetCreatePaymentIntentState() {
        _createPaymentIntentState.value = NetworkState.Init
    }

    fun createOrder(
        addressId: Int,
        deliveryPrice: Long,
        cartItems: List<CartItem>
    ) {
        if (_createOrderState.value is NetworkState.Loading) return
        viewModelScope.launch {
            createOrderUseCase.execute(
                addressId = addressId,
                deliveryPrice = deliveryPrice,
                cartItems = cartItems
            ).collect { state ->
                _createOrderState.value = state
            }
        }
    }

    fun resetCreateOrderState() {
        _createOrderState.value = NetworkState.Init
    }
}

