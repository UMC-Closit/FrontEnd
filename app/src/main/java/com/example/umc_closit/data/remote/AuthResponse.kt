package com.example.umc_closit.data.remote

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val clositId: String,
    val birth: String,
    val profileImage: String
)

data class RegisterResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: UserInfo?
)

data class UserInfo(
    val name: String,
    val email: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TokenResult?
)

data class TokenResult(
    val accessToken: String,
    val refreshToken: String
)