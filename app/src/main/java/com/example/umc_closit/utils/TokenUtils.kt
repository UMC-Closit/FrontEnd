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

        if (refreshToken.isEmpty()) {
            // refreshToken 없으면 로그인으로
            moveToLogin(context)
            return
        }

        val request = RefreshRequest(refreshToken = refreshToken)

        RetrofitClient.authService.refreshToken(request)
            .enqueue(object : Callback<RefreshResponse> {
                override fun onResponse(call: Call<RefreshResponse>, response: Response<RefreshResponse>) {
                    if (response.isSuccessful) {
                        val newAccessToken = response.body()?.result?.accessToken ?: ""
                        val newRefreshToken = response.body()?.result?.refreshToken ?: ""

                        with(sharedPreferences.edit()) {
                            putString("accessToken", newAccessToken)
                            putString("refreshToken", newRefreshToken)
                            apply()
                        }

                        Log.d("TOKEN","어세스 토큰 재발급 완료")
                        // 새 토큰 저장 후 원래 API 재시도
                        handleTokenRefresh(
                            retryCall(),
                            onSuccess,
                            onFailure,
                            retryCall,
                            context
                        )
                    } else {
                        // refreshToken도 만료 → 로그인 이동
                        moveToLogin(context)
                    }
                }

                override fun onFailure(call: Call<RefreshResponse>, t: Throwable) {
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

    fun getUserId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", -1)
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