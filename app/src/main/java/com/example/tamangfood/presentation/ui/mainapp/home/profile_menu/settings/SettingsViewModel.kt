package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.DeleteAccountUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _deleteAccountState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val deleteAccountState: MutableStateFlow<NetworkState> = _deleteAccountState

    fun resetDeleteAccountState() {
        _deleteAccountState.value = NetworkState.Init
    }

    fun deleteAccount(userId: Int) {
        _deleteAccountState.value = NetworkState.Loading
        viewModelScope.launch {
            deleteAccountUseCase.execute(userId).collect { _deleteAccountState.value = it }
        }
    }
}
