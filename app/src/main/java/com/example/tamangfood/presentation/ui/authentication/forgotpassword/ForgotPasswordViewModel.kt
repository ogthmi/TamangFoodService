package com.example.tamangfood.presentation.ui.authentication.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.OtpUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: OtpUseCase
): ViewModel() {

    private var _forgotPasswordState = MutableStateFlow<NetworkState>(NetworkState.Init)

    val forgotPasswordState: MutableStateFlow<NetworkState> = _forgotPasswordState

    var email: String? = null

    fun forgotPassword(email: String){
        this.email = email

        _forgotPasswordState.value = NetworkState.Loading

        viewModelScope.launch {
            forgotPasswordUseCase
                .execute(email = email)
                .collect { _forgotPasswordState.value = it }
        }
    }

    fun resetState() {
        _forgotPasswordState.value = NetworkState.Init
    }
}