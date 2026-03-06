package com.example.tamangfood.presentation.ui.signup

import androidx.lifecycle.ViewModel
import com.example.tamangfood.data.model.auth.SignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    fun signUp(request: SignUpRequest) {
        validate(request)
    }

    private fun validate(request: SignUpRequest) {
        if (request.fullName.isBlank()) {
            throw IllegalArgumentException("Full name cannot be empty")
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
            throw IllegalArgumentException("Invalid email address")
        }

        if (request.password.length < 8) {
            throw IllegalArgumentException("Password must be at least 6 characters")
        }

        if (request.phoneNumber.length < 10) {
            throw IllegalArgumentException("Invalid phone number")
        }

        if (request.dateOfBirth.isBlank()) {
            throw IllegalArgumentException("Please select date of birth")
        }
    }
}