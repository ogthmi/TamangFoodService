package com.example.tamangfood.presentation.ui.signin

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tamangfood.data.model.auth.SignInRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {
    private val _errorEmail = MutableLiveData<String?>()
    val errorEmail: LiveData<String?> = _errorEmail

    private val _errorPassword = MutableLiveData<String?>()
    val errorPassword: LiveData<String?> = _errorPassword

    private val _passwordVisible = MutableLiveData(false)
    val passwordVisible: LiveData<Boolean> = _passwordVisible

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess


    fun togglePassword() {
        _passwordVisible.value = !(_passwordVisible.value ?: false)
    }

    fun signIn(request: SignInRequest) {
        if (request.email.isEmpty()) {
            _errorEmail.value = "Email is required"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
            _errorEmail.value = "Invalid email"
            return
        }

        if (request.password.isEmpty()) {
            _errorPassword.value = "Password is required"
            return
        }

        // TODO: Call API later

        _loginSuccess.value = true
    }
}