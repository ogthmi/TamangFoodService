package com.example.tamangfood.presentation.ui.mainapp.home.cart.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.GetPaymentMethodsUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CardSelectionViewModel @Inject constructor(
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase
) : ViewModel() {
    private val _paymentMethodsState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val paymentMethodsState = _paymentMethodsState.asStateFlow()

    fun loadPaymentMethods() {
        if (_paymentMethodsState.value is NetworkState.Loading) return
        viewModelScope.launch {
            getPaymentMethodsUseCase.execute().collect { state ->
                _paymentMethodsState.value = state
            }
        }
    }
}
