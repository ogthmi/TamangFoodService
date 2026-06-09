package com.example.tamangfood.data.model.auth.signup

import com.example.tamangfood.data.model.auth.signin.Result

data class SignUpResponse (
    val code: Int,
    val message: String,
    val result: Result
)


