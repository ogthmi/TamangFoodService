package com.example.tamangfood.presentation.ui.authentication.signin

import androidx.lifecycle.ViewModel
import com.example.tamangfood.data.model.auth.SignInRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {
    fun signIn(request: SignInRequest) {
        // TODO: Call API later
    }
}