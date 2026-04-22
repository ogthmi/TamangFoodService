package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.model.Card
import com.example.tamangfood.domain.usecase.DeletePaymentMethodUseCase
import com.example.tamangfood.domain.usecase.GetPaymentMethodsUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PaymentMethodsViewModel @Inject constructor(
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase,
    private val deletePaymentMethodUseCase: DeletePaymentMethodUseCase
) : ViewModel() {
    private val _paymentMethodsState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val paymentMethodsState = _paymentMethodsState.asStateFlow()
    private val _deletePaymentMethodState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val deletePaymentMethodState = _deletePaymentMethodState.asStateFlow()

    fun loadPaymentMethods() {
        if (_paymentMethodsState.value is NetworkState.Loading) return
        viewModelScope.launch {
            getPaymentMethodsUseCase.execute().collect {
                _paymentMethodsState.value = it
            }
        }
    }

    fun resetPaymentMethodsState() {
        _paymentMethodsState.value = NetworkState.Init
    }

    fun deletePaymentMethod(paymentMethodId: String) {
        if (_deletePaymentMethodState.value is NetworkState.Loading) return
        viewModelScope.launch {
            deletePaymentMethodUseCase.execute(paymentMethodId).collect {
                _deletePaymentMethodState.value = it
            }
        }
    }

    fun resetDeletePaymentMethodState() {
        _deletePaymentMethodState.value = NetworkState.Init
    }
}