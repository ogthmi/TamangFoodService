package com.example.tamangfood.presentation.ui.authentication.signin

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.tamangfood.data.model.auth.signin.SignInRequest
import com.example.tamangfood.domain.repository.SignInRepository
import com.example.tamangfood.domain.usecase.SignInUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    private val _signInState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val signInState: MutableStateFlow<NetworkState> = _signInState

    fun signIn(email: String, password: String) {
        _signInState.value = NetworkState.Loading
        viewModelScope.launch {
            signInUseCase.execute(
                email = email,
                password = password
            ).collect {
                _signInState.value = it
            }
        }
    }
}