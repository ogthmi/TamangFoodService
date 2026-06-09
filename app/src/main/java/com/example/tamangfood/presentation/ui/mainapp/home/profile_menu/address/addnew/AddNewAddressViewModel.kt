package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.address.addnew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.AddAddressUseCase
import com.example.tamangfood.domain.usecase.DeleteAddressUseCase
import com.example.tamangfood.domain.usecase.GetAddressByIdUseCase
import com.example.tamangfood.domain.usecase.UpdateAddressUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddNewAddressViewModel @Inject constructor(
    private val addAddressUseCase: AddAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val getAddressByIdUseCase: GetAddressByIdUseCase
) : ViewModel() {

    private val _addAddressState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val addAddressState: MutableStateFlow<NetworkState> = _addAddressState

    private val _addressByIdState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val addressByIdState: MutableStateFlow<NetworkState> = _addressByIdState

    private val _updateAddressState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val updateAddressState: MutableStateFlow<NetworkState> = _updateAddressState

    private val _deleteAddressState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val deleteAddressState: MutableStateFlow<NetworkState> = _deleteAddressState

    fun resetAddAddressState() {
        _addAddressState.value = NetworkState.Init
    }

    fun resetAddressByIdState() {
        _addressByIdState.value = NetworkState.Init
    }

    fun loadAddressById(addressId: Int) {
        if (_addressByIdState.value is NetworkState.Loading) return
        viewModelScope.launch {
            getAddressByIdUseCase.execute(addressId).collect {
                _addressByIdState.value = it
            }
        }
    }

    fun addAddress(address: String, latitude: Double, longitude: Double) {
        if (_addAddressState.value is NetworkState.Loading) return
        viewModelScope.launch {
            _addAddressState.value = NetworkState.Loading
            addAddressUseCase.execute(address, latitude, longitude).collect {
                _addAddressState.value = it
            }
        }
    }

    fun updateAddress(
        addressId: Int,
        address: String,
        latitude: Double,
        longitude: Double
    ) {
        if (_updateAddressState.value is NetworkState.Loading) return
        viewModelScope.launch {
            _updateAddressState.value = NetworkState.Loading
            updateAddressUseCase.execute(addressId, address, latitude, longitude).collect {
                _updateAddressState.value = it
            }
        }
    }

    fun resetUpdateAddressState() {
        _updateAddressState.value = NetworkState.Init
    }

    fun deleteAddress(addressId: Int) {
        if (_deleteAddressState.value is NetworkState.Loading) return
        viewModelScope.launch {
            _deleteAddressState.value = NetworkState.Loading
            deleteAddressUseCase.execute(addressId).collect {
                _deleteAddressState.value = it
            }
        }
    }

    fun resetDeleteAddressState() {
        _deleteAddressState.value = NetworkState.Init
    }
}
