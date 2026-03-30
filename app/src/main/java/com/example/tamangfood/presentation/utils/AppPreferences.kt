package com.example.tamangfood.presentation.utils

import android.content.Context
import android.content.SharedPreferences

class AppPreferences {
    companion object {
        private const val PREF_NAME = "app_prefs"
        private const val TOKEN_REF = "token_refs"
        private const val REFRESH_TOKEN_REF = "refresh_token_refs"
        private const val USER_REF = "user_refs"
        private const val SESSION_EXPIRED_REF = "session_expired_ref"
        private lateinit var sharedPref: SharedPreferences

        fun init(context: Context) {
            sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        fun saveToken(token: String) {
            sharedPref.edit().putString(TOKEN_REF, token).apply()
        }


        fun getToken(): String? {
            return sharedPref.getString(TOKEN_REF, null)
        }

        fun saveRefreshToken(token: String) {
            sharedPref.edit().putString(REFRESH_TOKEN_REF, token).apply()
        }

        fun getRefreshToken(): String? {
            return sharedPref.getString(REFRESH_TOKEN_REF, null)
        }

        fun saveUserId(id: Int){
            sharedPref.edit().putInt(USER_REF, id).apply()
        }

        fun getUserId(): Int?{
            return sharedPref.getInt(USER_REF, -1)
        }

        fun hasSession(): Boolean {
            return !getToken().isNullOrBlank() || !getRefreshToken().isNullOrBlank()
        }

        fun clearSession() {
            sharedPref.edit()
                .remove(TOKEN_REF)
                .remove(REFRESH_TOKEN_REF)
                .remove(USER_REF)
                .apply()
        }

        fun markSessionExpired() {
            sharedPref.edit().putBoolean(SESSION_EXPIRED_REF, true).apply()
        }

        fun consumeSessionExpired(): Boolean {
            val isExpired = sharedPref.getBoolean(SESSION_EXPIRED_REF, false)
            if (isExpired) {
                sharedPref.edit().putBoolean(SESSION_EXPIRED_REF, false).apply()
            }
            return isExpired
        }

    }
}