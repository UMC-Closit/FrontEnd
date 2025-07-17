package com.example.umc_closit.data.remote

import android.content.Context
import android.util.Log
import com.example.umc_closit.utils.TokenUtils
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    companion object {
        @Volatile private var isRefreshing = false
        private val requestQueue = mutableListOf<() -> Unit>()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 예외 URL은 그냥 통과
        val excludedEndpoints = listOf("/api/auth/login", "/api/auth/register", "/api/auth/refresh")
        if (excludedEndpoints.any { originalRequest.url.encodedPath.contains(it) }) {
            return chain.proceed(originalRequest)
        }

        val accessToken = TokenUtils.getAccessToken(context) ?: ""
        val authRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(authRequest)

        if (response.code != 401) return response

        response.close()

        // 401 → 재발급 로직 진입
        synchronized(this) {
            if (!isRefreshing) {
                isRefreshing = true

                // 실제 refresh 토큰 API 요청
                val success = TokenUtils.refreshTokenSync(context)

                isRefreshing = false

                if (success) {
                    val newAccessToken = TokenUtils.getAccessToken(context) ?: ""
                    val previousToken = accessToken

                    Log.d("TOKEN_DEBUG", "🔎 이전 토큰: $previousToken")
                    Log.d("TOKEN_DEBUG", "🔎 새로 받은 토큰: $newAccessToken")

                    if (newAccessToken == previousToken) {
                        Log.e("TOKEN_DEBUG", "🚫 새 토큰이 이전 토큰과 동일 → 무한 루프 방지 차단")
                        return response
                    }

                    Log.d("TOKEN_DEBUG", "🔐 재발급 후 새로운 AccessToken으로 요청 → $newAccessToken")

                    val newRequest = originalRequest.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()

                    requestQueue.forEach { it() }
                    requestQueue.clear()

                    return chain.proceed(newRequest)
                } else {
                    TokenUtils.moveToLogin(context)
                    return response // 재발급 실패
                }
            } else {
                // 재발급 중이면 큐에 추가하고 기다림
                val latch = java.util.concurrent.CountDownLatch(1)
                var finalResponse: Response? = null

                requestQueue.add {
                    val latestToken = TokenUtils.getAccessToken(context) ?: ""
                    Log.d("TOKEN_DEBUG", "🔁 재시도 요청에 최신 토큰 사용 → $latestToken")

                    val newRequest = originalRequest.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $latestToken")
                        .build()

                    try {
                        finalResponse = chain.proceed(newRequest)
                    } catch (e: Exception) {
                        Log.e("TOKEN_DEBUG", "❌ 재시도 중 예외 발생: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }

                latch.await()
                return finalResponse ?: response
            }
        }
    }
}
