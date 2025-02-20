package com.example.umc_closit.data.remote.battle

data class ChallengeBattleResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ChallengeBattleResult?
)

data class ChallengeBattleResult(
    val challengeBattlePreviewList: List<ChallengeBattlePreview>,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)

data class ChallengeBattlePreview(
    val battleId: Long,
    val firstClositId: String,
    val firstProfileImage: String,
    val firstPostId: Int,
    val firstPostFrontImage: String,
    val firstPostBackImage: String,
    val title: String
)

