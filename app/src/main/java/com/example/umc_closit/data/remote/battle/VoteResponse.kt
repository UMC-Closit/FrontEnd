package com.example.umc_closit.data.remote.battle

import com.google.gson.annotations.SerializedName

// 서버에서 반환하는 투표 응답 데이터를 담는 클래스
data class VoteResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: VoteResult?
)

// 투표 결과 데이터
data class VoteResult(
    @SerializedName("battleId") val battleId: Long,
    @SerializedName("firstClositId") val firstClosetId: String,
    @SerializedName("firstVotingRate") val firstVotingRate: Float,
    @SerializedName("secondClositId") val secondClosetId: String,
    @SerializedName("secondVotingRate") val secondVotingRate: Float,
    @SerializedName("createdAt") val createdAt: String
)


// 좋아요 응답 데이터
data class LikeResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: LikeResult?
)

// 좋아요 결과 데이터
data class LikeResult(
    @SerializedName("battleLikeId") val battleLikeId: Long,
    @SerializedName("createdAt") val createdAt: String
)
