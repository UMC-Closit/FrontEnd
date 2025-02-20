package com.example.umc_closit.data.remote.profile


// follow
data class FollowRequest(
    val receiver: String
)

data class FollowResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: FollowResult?
)

data class FollowResult(
    val followId: Int,
    val senderId: Int,
    val receiverId: Int,
    val createdAt: String
)

// unfollow
data class UnfollowResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String
)

// isfollowing
data class FollowCheckResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean
)

// profile info
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
    val profileImage: String?,
    val followers: Int,
    val following: Int,
    val createdAt: String
)

// edit profile
data class EditProfileRequest(
    val name: String,
    val currentPassword: String,
    val password: String,
    val birth: String
)
