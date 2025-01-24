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
    val hashtags: List<String> // 추가된 필드
) : Parcelable