package com.example.umc_closit.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimelineItem(
    val id: Int,
    val mainImageResId: Int,
    val overlayImageResId: Int,
    val userProfileResId: Int,
    val userName: String,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val postText: String,

    val hashtags: List<String>, // 추가된 필드

    val uploadDate: String,  // 업로드 날짜 (예: "2025-01-17T06:52:07.831513")
    val pointColor: String   // 포인트 색상 코드 (예: "#FF5733")

) : Parcelable

