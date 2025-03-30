// BattlePostRequest.kt
package com.example.umc_closit.data

import com.google.gson.annotations.SerializedName

data class BattlePostRequest(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String
)

data class VoteRequest(
    val postId: Int
)


