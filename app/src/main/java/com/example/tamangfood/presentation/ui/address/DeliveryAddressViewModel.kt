package com.example.tamangfood.presentation.ui.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.data.model.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryAddressViewModel @Inject constructor(
    // TODO: Inject AddressRepository when available
) : ViewModel() {

    private val _addresses = MutableLiveData<List<Address>>()
    val addresses: LiveData<List<Address>> = _addresses

    fun loadAddresses() {
        viewModelScope.launch {
            try {
                // TODO: Implement actual load logic with repository
                // For now, we'll use sample data
                val sampleAddresses = listOf(
                    Address(
                        id = 1,
                        name = "My home",
                        fullAddress = "778 Locust View Drive Oakland, CA",
                        latitude = 37.7749,
                        longitude = -122.4194,
                        isSelected = false
                    ),
                    Address(
                        id = 2,
                        name = "My Office",
                        fullAddress = "778 Locust View Drive Oakland, CA",
                        latitude = 37.7849,
                        longitude = -122.4094,
                        isSelected = true
                    ),
                    Address(
                        id = 3,
                        name = "Parent's House",
                        fullAddress = "778 Locust View Drive Oakland, CA",
                        latitude = 37.7649,
                        longitude = -122.4294,
                        isSelected = false
                    )
                )
                _addresses.value = sampleAddresses
            } catch (e: Exception) {
                _addresses.value = emptyList()
            }
        }
    }

    fun selectAddress(address: Address) {
        viewModelScope.launch {
            val currentAddresses = _addresses.value ?: emptyList()
            val updatedAddresses = currentAddresses.map {
                it.copy(isSelected = it.id == address.id)
            }
            _addresses.value = updatedAddresses
        }
    }
}

