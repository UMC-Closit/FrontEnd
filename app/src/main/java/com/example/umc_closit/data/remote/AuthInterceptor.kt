package com.example.umc_closit.data.remote

import android.content.Context
import android.util.Log
import com.example.umc_closit.utils.TokenUtils
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val excludedEndpoints = listOf(
            "/api/auth/users/unique",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh"
        )

        if (excludedEndpoints.any { request.url.encodedPath.contains(it) }) {
            return chain.proceed(request) // 헤더 안 붙이고 그대로 진행
        }

        val token = TokenUtils.getAccessToken(context) ?: ""
        Log.d("TOKEN_DEBUG", "🔍 일괄 요청에 사용된 AccessToken: $token") // ★ 여기에 추가
        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}
