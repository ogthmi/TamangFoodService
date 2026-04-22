package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.payment.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.CreatePaymentMethodUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val createPaymentMethodUseCase: CreatePaymentMethodUseCase
) : ViewModel() {
    private val _createPaymentMethodState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val createPaymentMethodState = _createPaymentMethodState.asStateFlow()

    fun savePaymentMethod(paymentMethodId: String) {
        if (_createPaymentMethodState.value is NetworkState.Loading) return
        viewModelScope.launch {
            createPaymentMethodUseCase.execute(paymentMethodId).collect { state ->
                _createPaymentMethodState.value = state
            }
        }
    }

    fun resetCreatePaymentMethodState() {
        _createPaymentMethodState.value = NetworkState.Init
    }
}