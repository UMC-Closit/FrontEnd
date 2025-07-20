package com.example.umc_closit.data.remote.battle

import com.google.gson.annotations.SerializedName

data class BattleListResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: BattleListResult?
)

data class BattleListResult(
    @SerializedName("battlePreviewList") val battlePreviewList: List<BattlePreview>,
    @SerializedName("listSize") val listSize: Int,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("first") val first: Boolean,
    @SerializedName("last") val last: Boolean
)

data class BattlePreview(
    @SerializedName("battleId") val battleId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("firstClositId") val firstClositId: String,
    @SerializedName("firstProfileImage") val firstProfileImage: String,
    @SerializedName("firstPostId") val firstPostId: Int,
    @SerializedName("firstPostFrontImage") val firstPostFrontImage: String,
    @SerializedName("firstPostBackImage") val firstPostBackImage: String,
    @SerializedName("firstVotingRate") val firstVotingRate: Float,
    @SerializedName("secondClositId") val secondClositId: String,
    @SerializedName("secondProfileImage") val secondProfileImage: String,
    @SerializedName("secondPostId") val secondPostId: Int,
    @SerializedName("secondPostFrontImage") val secondPostFrontImage: String,
    @SerializedName("secondPostBackImage") val secondPostBackImage: String,
    @SerializedName("secondVotingRate") val secondVotingRate: Float,
    @SerializedName("liked") val liked: Boolean
)

