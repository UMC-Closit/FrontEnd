package com.example.umc_closit.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.umc_closit.data.remote.auth.RefreshRequest
import com.example.umc_closit.data.remote.auth.RefreshResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okio.Timeout

object TokenUtils {

    private const val PREFS_NAME = "auth_prefs"

    private var isRefreshing = false
    private val pendingTasks = mutableListOf<() -> Unit>()

    fun <T> handleTokenRefresh(
        call: Call<T>,
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit,
        context: Context
    ) {
        Log.d("TOKEN_DEBUG", "🚀 API 요청 시작 → ${call.request().url}")
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    Log.d("TOKEN_DEBUG", "✅ API 요청 성공 → ${call.request().url}")
                    response.body()?.let { onSuccess(it) }
                } else if (response.code() == 401) {
                    val usedToken = getAccessToken(context) ?: ""
                    Log.w("TOKEN_DEBUG", "⚠️ 401 Unauthorized → 토큰 만료 감지, 요청 URL: ${call.request().url}")
                    Log.d("TOKEN_DEBUG", "🚨 사용된 AccessToken: $usedToken")
                    enqueuePendingTask {
                        Log.d("TOKEN_DEBUG", "🔁 재시도 API → ${call.request().url}")
                        handleTokenRefresh(call.clone(), onSuccess, onFailure, context)
                    }

                    if (!isRefreshing) {
                        refreshTokenOnly(
                            context = context,
                            onSuccess = {
                                Log.d("TOKEN_DEBUG", "🔄 재발급 완료 → 모든 요청 재시도 시작")
                                executePendingTasks()
                            },
                            onFailure = {
                                Log.e("TOKEN_DEBUG", "❌ 재발급 실패 → 모든 요청 실패 처리 & 로그인 이동")
                                failPendingTasks(it)
                                moveToLogin(context)
                            }
                        )
                    } else {
                        Log.d("TOKEN_DEBUG", "⏳ 재발급 대기 중 → 요청 큐에 보관됨")
                    }
                } else {
                    Log.e("TOKEN_DEBUG", "❌ API 실패 (${response.code()}) → ${call.request().url}")
                    onFailure(Throwable("API 실패: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.e("TOKEN_DEBUG", "🌐 API 네트워크 실패: ${t.message}")
                onFailure(t)
            }
        })
    }

    private fun enqueuePendingTask(task: () -> Unit) {
        Log.d("TOKEN_DEBUG", "📦 요청 큐에 추가됨 (총 ${pendingTasks.size + 1}개)")
        pendingTasks.add(task)
    }

    private fun executePendingTasks() {
        val tasks = pendingTasks.toList()
        pendingTasks.clear()
        isRefreshing = false
        Log.d("TOKEN_DEBUG", "🚀 요청 큐 ${tasks.size}개 재시도 시작")
        tasks.forEach { it() }
    }

    private fun failPendingTasks(t: Throwable) {
        val tasks = pendingTasks.toList()
        pendingTasks.clear()
        isRefreshing = false
        Log.e("TOKEN_DEBUG", "💥 요청 ${tasks.size}개 실패 처리됨")
        tasks.forEach {
            it.invoke() // 실패 시 실행해도 실패 콜백 호출되도록 구성돼야 함
        }
    }

    fun refreshTokenOnly(
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val refreshToken = sharedPreferences.getString("refreshToken", "") ?: ""

        if (isRefreshing) {
            Log.w("TOKEN_DEBUG", "⛔ 이미 재발급 중 → 중복 요청 무시")
            return
        }

        if (refreshToken.isEmpty()) {
            Log.e("TOKEN_DEBUG", "❌ RefreshToken 없음 → 로그인 이동")
            moveToLogin(context)
            return
        }

        isRefreshing = true
        Log.d("TOKEN_DEBUG", "🔁 RefreshToken 요청 시작")

        val request = RefreshRequest(refreshToken)
        RetrofitClient.authService.refreshToken(request)
            .enqueue(object : Callback<RefreshResponse> {
                override fun onResponse(call: Call<RefreshResponse>, response: Response<RefreshResponse>) {
                    if (response.isSuccessful) {
                        val access = response.body()?.result?.accessToken ?: ""
                        val refresh = response.body()?.result?.refreshToken ?: ""

                        saveTokens(context, access, refresh, getClositId(context) ?: "")

                        Log.d("TOKEN_DEBUG", "✅ 새 토큰 저장 완료 → access: $access")
                        onSuccess()
                    } else {
                        Log.e("TOKEN_DEBUG", "❌ RefreshToken 응답 실패 → 로그인 이동")
                        onFailure(Throwable("토큰 재발급 실패: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<RefreshResponse>, t: Throwable) {
                    Log.e("TOKEN_DEBUG", "❌ RefreshToken API 실패: ${t.message}")
                    onFailure(t)
                }
            })
    }

    fun refreshTokenSync(context: Context): Boolean {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val refreshToken = prefs.getString("refreshToken", "") ?: ""

        if (refreshToken.isEmpty()) {
            Log.e("TOKEN_DEBUG", "❌ RefreshToken 없음 → 로그인 이동")
            moveToLogin(context)
            return false
        }

        val request = RefreshRequest(refreshToken)

        return try {
            val response = RetrofitClient.authService.refreshToken(request).execute()
            if (response.isSuccessful) {
                val newAccessToken = response.body()?.result?.accessToken ?: ""
                val newRefreshToken = response.body()?.result?.refreshToken ?: ""

                val previousToken = getAccessToken(context)
                Log.d("TOKEN_DEBUG", "🔎 이전 토큰: $previousToken")
                Log.d("TOKEN_DEBUG", "🔎 새로 받은 토큰: $newAccessToken")
                if (newAccessToken == previousToken) {
                    Log.e("TOKEN_DEBUG", "🚫 새 토큰이 이전 토큰과 동일 → 무한 루프 방지 차단")
                    return false
                }

                saveTokens(context, newAccessToken, newRefreshToken, getClositId(context) ?: "")

                Log.d("TOKEN_DEBUG", "✅ 동기 재발급 성공 → AccessToken: $newAccessToken")
                true
            } else {
                Log.e("TOKEN_DEBUG", "❌ 동기 재발급 실패 → 로그인 이동")
                moveToLogin(context)
                false
            }
        } catch (e: Exception) {
            Log.e("TOKEN_DEBUG", "❌ 예외 발생: ${e.message}")
            moveToLogin(context)
            false
        }
    }
    private var accessTokenCache: String? = null
    private var refreshTokenCache: String? = null

    fun saveTokens(context: Context, accessToken: String, refreshToken: String, clositId: String) {
        accessTokenCache = accessToken
        refreshTokenCache = refreshToken
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("accessToken", accessToken)
            putString("refreshToken", refreshToken)
            putString("clositId", clositId)
            putBoolean("isLoggedIn", true)
            commit()
        }
        Log.d("TOKEN_DEBUG", "✅ 새 토큰 저장 완료 → access: $accessToken")
    }

    fun getAccessToken(context: Context): String? {
        if (accessTokenCache != null) return accessTokenCache
        val token = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("accessToken", null)
        accessTokenCache = token
        return token
    }

    fun getRefreshToken(context: Context): String? {
        if (refreshTokenCache != null) return refreshTokenCache
        val token = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("refreshToken", null)
        refreshTokenCache = token
        return token
    }


    fun moveToLogin(context: Context) {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    fun getClositId(context: Context): String? {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getString("clositId", "")
    }

    fun isLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getBoolean("isLoggedIn", false)
    }

    fun clearTokens(context: Context) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
    }
}