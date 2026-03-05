package com.example.tamangfood.presentation.ui.signin

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {

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


    fun signIn(email: String, password: String) {

        _errorEmail.value = null
        _errorPassword.value = null

        if (email.isEmpty()) {
            _errorEmail.value = "Email is required"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorEmail.value = "Invalid email"
            return
        }

        if (password.isEmpty()) {
            _errorPassword.value = "Password is required"
            return
        }

        // TODO: call API

        _loginSuccess.value = true
    }
}