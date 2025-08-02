package com.example.umc_closit.data.entities

data class BattleItem(
    val id: Int,
    val battleId: Long,
    val userName: String,
    val userProfileUrl: String,
    val leftPostId: Int,
    val rightPostId: Int,
    val battleLikeId: Int,
    val leftPostImageUrl: String,
    val rightPostImageUrl: String,
    val likeCount: Int,
    val firstPostBackImage: String,
    val secondPostBackImage: String,
    val firstVotingCnt: Int,
    val secondVotingCnt: Int,
    val firstClositId: String,
    val secondClositId: String,
    val firstProfileImage: String,
    val secondProfileImage: String
)