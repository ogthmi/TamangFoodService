package com.example.tamangfood.presentation.ui.mainapp.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.model.Order
import com.example.tamangfood.domain.usecase.GetOrdersByStatusUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrdersByStatusData(
    val status: OrderStatus,
    val orders: List<Order>
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getOrdersByStatusUseCase: GetOrdersByStatusUseCase
) : ViewModel() {
    private val _ordersState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val ordersState = _ordersState.asStateFlow()

    fun loadOrders(status: OrderStatus, userId: Int) {
        if (userId <= 0) {
            _ordersState.value = NetworkState.Error("Invalid user id")
            return
        }
        viewModelScope.launch {
            getOrdersByStatusUseCase.execute(status, userId).collect { state ->
                _ordersState.value = when (state) {
                    is NetworkState.Success<*> -> {
                        val orders = (state.data as? List<*>)?.filterIsInstance<Order>().orEmpty()
                        NetworkState.Success(OrdersByStatusData(status, orders))
                    }
                    is NetworkState.Error -> state
                    is NetworkState.Loading -> state
                    is NetworkState.Init -> state
                }
            }
        }
    }
}