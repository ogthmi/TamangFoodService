package com.example.tamangfood.presentation.utils

import android.content.Context
import android.content.SharedPreferences

class AppPreferences {
    companion object {
        private const val TOKEN_REF = "token_refs"
        private lateinit var sharedPref: SharedPreferences

        fun init(context: Context) {
            sharedPref = context.getSharedPreferences(TOKEN_REF, Context.MODE_PRIVATE)
        }

        fun saveToken(token: String) {
            sharedPref.edit().putString(TOKEN_REF, token).apply()
        }


        fun getToken(): String? {
            return sharedPref.getString(TOKEN_REF, null)
        }
    }
}