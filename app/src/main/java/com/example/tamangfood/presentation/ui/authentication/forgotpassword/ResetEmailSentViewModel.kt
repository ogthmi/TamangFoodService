package com.example.tamangfood.presentation.ui.authentication.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.ResetPasswordUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetEmailSentViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private var _resetPasswordState = MutableStateFlow<NetworkState>(NetworkState.Init)

    val resetPasswordState: MutableStateFlow<NetworkState> = _resetPasswordState

    fun resetPassword(otp: String, email: String, newPassword: String, confirmPassword: String) {
        _resetPasswordState.value = NetworkState.Loading

        viewModelScope.launch {
            resetPasswordUseCase.execute(otp, email, newPassword, confirmPassword)
                .collect { _resetPasswordState.value = it }
        }
    }

    fun resetState() {
        _resetPasswordState.value = NetworkState.Init
    }
}