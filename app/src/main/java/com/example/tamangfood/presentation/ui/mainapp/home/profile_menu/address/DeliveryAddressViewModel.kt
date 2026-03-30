package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.GetUserAddressUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DeliveryAddressViewModel @Inject constructor(
    private val getUserAddressesUseCase: GetUserAddressUseCase
) : ViewModel() {

    private val _addressesState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val addressesState: MutableStateFlow<NetworkState> = _addressesState


    fun loadAddresses() {
        if (_addressesState.value is NetworkState.Loading) return
        viewModelScope.launch {
            getUserAddressesUseCase.execute().collect { _addressesState.value = it }
        }
    }
}
