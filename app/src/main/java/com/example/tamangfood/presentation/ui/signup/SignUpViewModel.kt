package com.example.tamangfood.presentation.ui.signup

import androidx.lifecycle.ViewModel
import com.example.tamangfood.data.model.auth.SignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    fun signUp(request: SignUpRequest) {
        //TO DO: Call API later
    }
}