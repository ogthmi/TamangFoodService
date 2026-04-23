package com.example.tamangfood.presentation.ui.mainapp.home.cart.confirmorder.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.GetUserAddressUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddressSelectionViewModel @Inject constructor(
    private val getUserAddressUseCase: GetUserAddressUseCase
) : ViewModel() {
    private val _addressesState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val addressesState = _addressesState.asStateFlow()

    fun loadAddresses() {
        if (_addressesState.value is NetworkState.Loading) return
        viewModelScope.launch {
            getUserAddressUseCase.execute().collect { state ->
                _addressesState.value = state
            }
        }
    }
}
