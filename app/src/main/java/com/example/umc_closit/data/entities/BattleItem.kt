package com.example.umc_closit.data.entities

// BattleItem: 배틀 게시글 데이터를 담는 데이터 클래스입니다.
data class BattleItem(
    val id: Long,
    val battleId: Long,
    val userName: String,
    val userProfileUrl: String,
    val leftPostId: Long,
    val rightPostId: Long,
    val battleLikeId: Long,
    var isLiked: Boolean = false // 좋아요 상태
)

