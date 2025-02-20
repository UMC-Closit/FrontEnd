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

// 북마크 조회
data class BookmarkResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: BookmarkResult
)

data class BookmarkResult(
    val bookmarkResultDTOList: List<BookmarkItem>,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)

data class BookmarkItem(
    val clositId: String,
    val userName: String,
    val bookmarkId: Int,
    val postId: Int,
    val thumbnail: String,
    val createdAt: String
)
