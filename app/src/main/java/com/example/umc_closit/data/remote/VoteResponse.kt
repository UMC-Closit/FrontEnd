package com.example.umc_closit.data

import com.google.gson.annotations.SerializedName

// 서버에서 반환하는 투표 응답 데이터를 담는 클래스
data class VoteResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: VoteResult
)

data class VoteResult(
    @SerializedName("battleId") val battleId: Long,
    @SerializedName("firstClosetId") val firstClosetId: String,
    @SerializedName("firstVotingRate") val firstVotingRate: Int,
    @SerializedName("secondClosetId") val secondClosetId: String,
    @SerializedName("secondVotingRate") val secondVotingRate: Int,
    @SerializedName("createdAt") val createdAt: String
)
data class LikeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LikeResult
)

data class LikeResult(
    val battleLikeId: Long,
    val createdAt: String
)
