// BattlePostResponse.kt
package com.example.umc_closit.data

import com.google.gson.annotations.SerializedName

data class BattlePostResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: BattlePostResult?
)

data class BattlePostResult(
    @SerializedName("battleId") val battleId: Int,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("deadline") val deadline: String,
    @SerializedName("createdAt") val createdAt: String
)

