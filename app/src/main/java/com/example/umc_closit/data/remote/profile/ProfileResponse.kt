package com.example.umc_closit.data.remote.profile

import com.example.umc_closit.data.remote.profile.history.DateHistoryThumbnail


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

// highlight
data class HighlightListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: HighlightListResult
)

data class HighlightListResult(
    val highlights: List<HighlightItem>,
    val hasNext: Boolean,
    val pageNumber: Int,
    val size: Int
)

data class HighlightItem(
    val clositId: String,
    val userName: String,
    val postId: Int,
    val thumbnail: String,
    val createdAt: String,
    val updatedAt: String? = null
)

data class HighlightDetailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: HighlightDetailResult
)

data class HighlightDetailResult(
    val highlightId: Int,
    val clositId: String,
    val createdAt: String,
    val updatedAt: String?,
    val post: HighlightPost
)

data class HighlightPost(
    val id: Int,
    val backImage: String,
    val createdAt: String
)


data class HighlightCreateResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: HighlightCreateResult
)

data class HighlightCreateResult(
    val highlightId: Int,
    val clositId: String,
    val postId: Int,
    val createdAt: String
)

data class HighlightDeleteResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String
)

