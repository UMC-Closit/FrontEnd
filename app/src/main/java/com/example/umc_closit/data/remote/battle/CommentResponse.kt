package com.example.umc_closit.data.remote.battle

data class CommentResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: CommentResult
)

data class CommentResult(
    val battleCommentPreviewList: List<BattleComment>,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)

data class BattleComment(
    val battleCommentId: Int,
    val clositId: String,
    val content: String,
    val createdAt: String
)

data class CommentRequest(
    val content: String
)

// 댓글 작성 응답 모델
data class CommentPostResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: CommentPostResult
)

data class CommentPostResult(
    val battleCommentId: Int,
    val clositId: String,
    val createdAt: String
)

data class DeleteCommentResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String
)