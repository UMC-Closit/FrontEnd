package com.example.umc_closit.data.remote.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {
    @POST("/api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/auth/refresh")
    fun refreshToken(
        @Body request: RefreshRequest
    ): Call<RefreshResponse>

    @GET("/api/auth/users/isunique/{closit_id}")
    fun checkIdUnique(
        @Path("closit_id") clositId: String
    ): Call<CheckIdResponse>

    @DELETE("/api/auth/users/")
    fun deleteUser(
    ): Call<QuitResponse<String>>
}