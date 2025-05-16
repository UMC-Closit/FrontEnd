package com.example.umc_closit.data.remote.challenge

data class ChallengeRequest(
    val postId: Int
)

data class ChallengeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ChallengeResult?
)

data class ChallengeResult(
    val firstClosItId: String,
    val firstPostId: Int,
    val secondClosItId: String,
    val secondPostId: Int,
    val createdAt: String
)
