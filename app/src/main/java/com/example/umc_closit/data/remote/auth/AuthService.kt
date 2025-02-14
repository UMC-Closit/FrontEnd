package com.example.umc_closit.data.remote.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/auth/refresh")
    fun refreshToken(
        @Body request: RefreshRequest
    ): Call<RefreshResponse>
}