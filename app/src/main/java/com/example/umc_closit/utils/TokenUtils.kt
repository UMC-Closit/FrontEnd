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

object TokenUtils {

    // private var isRefreshing = false

    fun <T> handleTokenRefresh(
        call: Call<T>,
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit,
        retryCall: () -> Call<T>, // 토큰 재발급 성공 후 다시 호출할 원래 API 콜백
        context: Context
    ) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else if (response.code() == 401) { // 🔥 토큰 만료
                    refreshToken(context, onSuccess, onFailure, retryCall)
                } else {
                    onFailure(Throwable("API 실패: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun <T> refreshToken(
        context: Context,
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit,
        retryCall: () -> Call<T>
    ) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val refreshToken = sharedPreferences.getString("refreshToken", "") ?: ""

/*        if (isRefreshing) {
            Log.e("TOKEN_DEBUG", "⛔ 이미 토큰 재발급 중 → 요청 무시")
            return
        }

        isRefreshing = true*/

        Log.d("TOKEN_DEBUG", "🔄 AccessToken 만료 → RefreshToken 요청 시작")
        Log.d("TOKEN_DEBUG", "📦 현재 보유 RefreshToken: $refreshToken")

        if (refreshToken.isEmpty()) {
            Log.e("TOKEN_DEBUG", "❌ RefreshToken 없음 → 로그인 이동")
            moveToLogin(context)
            //isRefreshing = false
            return
        }

        val request = RefreshRequest(refreshToken = refreshToken)
        Log.d("TOKEN_DEBUG", "🚀 RefreshToken API 요청 보냄")

        RetrofitClient.authService.refreshToken(request)
            .enqueue(object : Callback<RefreshResponse> {
                override fun onResponse(call: Call<RefreshResponse>, response: Response<RefreshResponse>) {
                    Log.d("TOKEN_DEBUG", "🌐 RefreshToken API 응답 수신")
                    Log.d("TOKEN_DEBUG", "HTTP 코드: ${response.code()}")
                    Log.d("TOKEN_DEBUG", "응답 바디: ${response.body()}")
                    Log.d("TOKEN_DEBUG", "에러 바디: ${response.errorBody()?.string()}")
                    Log.d("TOKEN_DEBUG","리프레시 토큰: ${refreshToken}")

                    if (response.isSuccessful) {
                        val newAccessToken = response.body()?.result?.accessToken ?: ""
                        val newRefreshToken = response.body()?.result?.refreshToken ?: ""

                        // 1. 새로 발급받은 토큰 로그
                        Log.d("TOKEN_DEBUG", "✅ 재발급 성공 → AccessToken: $newAccessToken")
                        Log.d("TOKEN_DEBUG", "✅ 재발급 성공 → RefreshToken: $newRefreshToken")

                        with(sharedPreferences.edit()) {
                            putString("accessToken", newAccessToken)
                            putString("refreshToken", newRefreshToken)
                            commit() // apply() 대신 즉시 저장
                        }


                        Log.d("TOKEN_DEBUG", "💾 새 토큰 SharedPreferences 저장 완료")

                        // 2. 저장 후 바로 SharedPreferences에서 꺼내서 확인
                        val savedAccessToken = sharedPreferences.getString("accessToken", null)
                        val savedRefreshToken = sharedPreferences.getString("refreshToken", null)

                        Log.d("TOKEN_DEBUG", "🔍 SharedPreferences 저장 확인 → AccessToken: $savedAccessToken")
                        Log.d("TOKEN_DEBUG", "🔍 SharedPreferences 저장 확인 → RefreshToken: $savedRefreshToken")

                        Log.d("TOKEN_DEBUG", "🔄 원래 API 재시도 시작")

                        handleTokenRefresh(
                            retryCall().also {
                                Log.d("TOKEN_DEBUG", "🔁 재시도할 API URL: ${it.request().url}")
                            },
                            onSuccess,
                            onFailure,
                            retryCall,
                            context
                        )
                    } else {
                        Log.e("TOKEN_DEBUG", "❌ 재발급 실패 → 로그인 이동")
                        moveToLogin(context)
                    }
                }

                override fun onFailure(call: Call<RefreshResponse>, t: Throwable) {
                    Log.e("TOKEN_DEBUG", "🌐 RefreshToken API 요청 실패: ${t.message}")
                    //isRefreshing = false
                    onFailure(t)
                }
            })
    }


    fun moveToLogin(context: Context) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // 토큰 삭제

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 모든 액티비티 제거 후 로그인 화면
        context.startActivity(intent)
    }

    private const val PREFS_NAME = "auth_prefs"

    fun saveTokens(context: Context, accessToken: String, refreshToken: String, clositId: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("accessToken", accessToken)
            putString("refreshToken", refreshToken)
            putString("clositId", clositId)
            putBoolean("isLoggedIn", true)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("accessToken", null)
    }

    fun getClositId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("clositId", "")
    }

    fun isLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun clearTokens(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }


}