package com.example.tamangfood.data.api

import com.example.tamangfood.presentation.utils.AppPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenRefreshHelper: TokenRefreshHelper
) : Interceptor {

    private val lock = Any()

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (shouldSkipAuth(original)) {
            return chain.proceed(original)
        }

        val accessBefore = AppPreferences.getToken()
        var request = original.newBuilder()
            .apply { addBearerIfPresent(this, accessBefore) }
            .build()

        if (original.header(HEADER_AUTH_RETRY) == "true") {
            return chain.proceed(request)
        }

        var response = chain.proceed(request)

        if (response.code != HTTP_UNAUTHORIZED && response.code != HTTP_FORBIDDEN) {
            return response
        }

        response.close()

        synchronized(lock) {
            val current = AppPreferences.getToken()
            if (current != null && current != accessBefore) {
                request = original.newBuilder()
                    .apply { addBearerIfPresent(this, current) }
                    .header(HEADER_AUTH_RETRY, "true")
                    .build()
                return chain.proceed(request)
            }

            val isRefreshed = tokenRefreshHelper.refreshSync()

            if (isRefreshed) {
                val newAccess = AppPreferences.getToken()

                val newRequest = original.newBuilder()
                    .apply { addBearerIfPresent(this, newAccess) }
                    .header(HEADER_AUTH_RETRY, "true")
                    .build()

                return chain.proceed(newRequest)
            }
            val unauthorizedRequest = original.newBuilder()
                .apply { addBearerIfPresent(this, accessBefore) }
                .header(HEADER_AUTH_RETRY, "true")
                .build()
            return chain.proceed(unauthorizedRequest)
        }
    }

    private fun shouldSkipAuth(request: okhttp3.Request): Boolean {
        val path = request.url.encodedPath
        return path.contains("/auth/log-in", ignoreCase = true) ||
            path.contains("/auth/sign-up", ignoreCase = true) ||
            path.contains("/auth/refresh", ignoreCase = true) ||
            path.contains("/auth/forgot", ignoreCase = true)
    }

    private fun addBearerIfPresent(builder: okhttp3.Request.Builder, token: String?) {
        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }
    }

    private companion object {
        const val HEADER_AUTH_RETRY = "X-Auth-Retry"
        const val HTTP_UNAUTHORIZED = 401
        const val HTTP_FORBIDDEN = 403
    }
}
