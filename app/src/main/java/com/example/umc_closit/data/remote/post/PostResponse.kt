package com.example.umc_closit.data.remote.post

data class PostResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: PostDetail
)

data class PostDetail(
    val postId: Int,
    val clositId: String,
    val username: String,
    val profileImage: String,
    val frontImage: String,
    val backImage: String,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val isHighlighted: Boolean,
    val hashtags: List<String>,
    val frontItemtags: List<ItemTag>,
    val backItemtags: List<ItemTag>,
    val pointColor: String,
    val visibility: String,
    val mission: Boolean
)

data class PostDeleteResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Map<String, Any> = emptyMap() // 빈 객체
)

data class ItemTag(
    val x: Int,
    val y: Int,
    val content: String
)

data class PostRequest(
    val frontImage: String,
    val backImage: String,
    val hashtags: List<String>,
    val frontItemtags: List<ItemTag>,
    val backItemtags: List<ItemTag>,
    val pointColor: String,
    val visibility: String,
    val mission: Boolean
)

data class PostUploadResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: PostUploadResult
)

data class PostUploadResult(
    val clositId: String,
    val postId: Int,
    val createdAt: String,
    val visibility: String
)

data class RecentPostResponse(
    val userRecentPostDTOList: List<UserRecentPostDTO>,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)

data class UserRecentPostDTO(
    val clositId: String,
    val userName: String,
    val postId: Int,
    val thumbnail: String,
    val createdAt: String
)