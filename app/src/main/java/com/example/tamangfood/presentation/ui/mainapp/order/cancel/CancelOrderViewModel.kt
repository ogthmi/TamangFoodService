package com.example.tamangfood.presentation.ui.mainapp.order.cancel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.CancelOrderUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CancelOrderViewModel @Inject constructor(
    private val cancelOrderUseCase: CancelOrderUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _cancelResult = MutableSharedFlow<NetworkState>()
    val cancelResult: SharedFlow<NetworkState> = _cancelResult.asSharedFlow()

    fun cancelOrder(orderId: Int) {
        viewModelScope.launch {
            cancelOrderUseCase.execute(orderId).collect { state ->
                when (state) {
                    is NetworkState.Loading -> _isLoading.value = true
                    else -> {
                        _isLoading.value = false
                        _cancelResult.emit(state)
                    }
                }
            }
        }
    }
}
