package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.settings.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.ChangePasswordUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordSettingsViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
): ViewModel() {

    private var _changePasswordState = MutableStateFlow<NetworkState>(NetworkState.Init)

    val changePasswordState  = _changePasswordState

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String){
        _changePasswordState.value = NetworkState.Loading

        viewModelScope.launch {
            changePasswordUseCase.execute(currentPassword, newPassword, confirmPassword)
                .collect { state -> _changePasswordState.value = state }
        }
    }

    fun resetState() {
        _changePasswordState.value = NetworkState.Init
    }
}