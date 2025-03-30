package com.example.umc_closit.data.remote.battle

import com.google.gson.annotations.SerializedName

data class BattleChallengeResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: BattleChallengeResult?
)

data class BattleChallengeResult(
    @SerializedName("firstClositId") val firstClositId: String,
    @SerializedName("firstPostId") val firstPostId: Int,
    @SerializedName("firstPostFrontImage") val firstPostFrontImage: String,
    @SerializedName("firstPostBackImage") val firstPostBackImage: String,
    @SerializedName("secondClositId") val secondClositId: String,
    @SerializedName("secondPostId") val secondPostId: Int,
    @SerializedName("secondPostFrontImage") val secondPostFrontImage: String,
    @SerializedName("secondPostBackImage") val secondPostBackImage: String,
    @SerializedName("createdAt") val createdAt: String
)

