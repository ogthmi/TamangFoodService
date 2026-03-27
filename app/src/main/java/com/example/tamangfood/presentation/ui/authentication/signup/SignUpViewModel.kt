package com.example.tamangfood.presentation.ui.authentication.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.SignUpUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {
    private val _signUpState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val signUpState: MutableStateFlow<NetworkState> = _signUpState

    fun signUp(
        email: String,
       password: String,
       fullName: String,
       phoneNumber: String,
       dateOfBirth: Long
    ) {
        _signUpState.value = NetworkState.Loading
        viewModelScope.launch {
            signUpUseCase.execute(
                email = email,
                password = password,
                fullName = fullName,
                phoneNumber = phoneNumber,
                dateOfBirth = dateOfBirth
            ).collect {
                _signUpState.value = it
            }
        }
    }
}