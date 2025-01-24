package com.example.umc_closit.data

data class UserInfo(
    val userId: String,   // 사용자 ID
    val userName: String, // 사용자 이름
    val userProfileImage: String? // 사용자 프로필 이미지 URL (옵션)
)