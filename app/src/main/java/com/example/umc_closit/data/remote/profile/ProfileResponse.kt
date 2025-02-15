package com.example.umc_closit.data.remote.profile

import com.example.umc_closit.data.remote.auth.TokenResult

data class ProfileUserResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ProfileUserResult
)

data class ProfileUserResult(
    val id: Int,
    val role: String,
    val clositId: String,
    val name: String,
    val email: String,
    val birth: String,
    val profileImage: String
)

data class FollowRequest(
    val follower: String,
    val following: String
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
    val result: TokenResult?
)