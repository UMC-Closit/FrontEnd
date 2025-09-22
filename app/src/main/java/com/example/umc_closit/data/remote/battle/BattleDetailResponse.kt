package com.example.umc_closit.data.remote.battle

import com.example.umc_closit.data.remote.BaseResponse

data class BattleDetailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: BattleDetailResult
)

data class BattleDetailResult(
    val battleId: Int,
    val title: String,
    val description: String,
    val deadline: String,
    val firstClositId: String,
    val firstProfileImage: String,
    val firstPostId: Int,
    val firstPostFrontImage: String,
    val firstPostBackImage: String,
    val firstVotingCnt: Int,
    val secondClositId: String,
    val secondProfileImage: String,
    val secondPostId: Int,
    val secondPostFrontImage: String,
    val secondPostBackImage: String,
    val secondVotingCnt: Int
)
