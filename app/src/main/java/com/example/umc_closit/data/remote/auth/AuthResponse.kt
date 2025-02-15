package com.example.umc_closit.data.remote.auth

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
    val result: RegisterResult?
)

data class RegisterResult(
    val clositId: String,
    val name: String,
    val email: String
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
    val result: LoginResult?
)

data class LoginResult(
    val clositId: String, // 서버에서 string으로 내려옴
    val accessToken: String,
    val refreshToken: String
)


data class TokenResult(
    val accessToken: String,
    val refreshToken: String
)

// refresh

data class RefreshRequest(
    val refreshToken: String
)


data class RefreshResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: RefreshResult?
)

data class RefreshResult(
    val clositId: String,
    val accessToken: String,
    val refreshToken: String
)
