package com.example.tamangfood.data.api

import com.example.tamangfood.data.model.FailedResponse
import com.example.tamangfood.data.model.auth.refresh.RefreshTokenRequest
import com.example.tamangfood.presentation.utils.SessionExpiredBus
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.HTTP
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshHelper @Inject constructor(
    private val refreshApi: RefreshApi
) {
    @Synchronized
    fun refreshSync(): Boolean {
        val refresh = AppPreferences.getRefreshToken() ?: return false
        return try {
            val response = refreshApi.refresh(RefreshTokenRequest(refreshToken = refresh)).execute()
            if (!response.isSuccessful) {
                val rawError = response.errorBody()?.string()
                val error = runCatching { Gson().fromJson(rawError, FailedResponse::class.java) }.getOrNull()
                val code = error?.code
                if (isRefreshTokenExpired(code)) {
                    AppPreferences.clearSession()
                    AppPreferences.markSessionExpired()
                    SessionExpiredBus.emit()
                }
                return false
            }

            val body = response.body() ?: return false
            if (body.code != HTTP.SUCCESS.status || body.result == null) {
                return false
            }

            val token = body.result
            AppPreferences.saveToken(token.accessToken)
            AppPreferences.saveRefreshToken(token.refreshToken)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun isRefreshTokenExpired(code: Int?): Boolean {
        return code == 1014
    }
}
