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
    val result: RegisterResult?
)

data class RegisterResult(
    val userId: Int,
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
    val accessToken: String,
    val refreshToken: String,
    val userId: Int
)


data class TokenResult(
    val accessToken: String,
    val refreshToken: String
)

data class FollowRequest(
    val follower: Int,
    val following: Int
)

data class FollowResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: FollowResult?
)

data class FollowResult(
    val followerId: Int,
    val followingId: Int,
    val createdAt: String
)

data class UnfollowResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String?
)

