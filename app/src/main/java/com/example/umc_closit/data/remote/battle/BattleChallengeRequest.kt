package com.example.umc_closit.data.remote.battle

import com.google.gson.annotations.SerializedName

data class BattleChallengeRequest(
    @SerializedName("postId") val postId: Int
)

