package com.example.umc_closit.data.remote.battle

// 오늘의 옷장 요청 데이터
data class TodayClosetResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TodayClosetResult
)

data class TodayClosetResult(
    val todayClosets: List<TodayClosetItem>,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)

data class TodayClosetItem(
    val todayClosetId: Int,
    val postId: Int,
    val frontImage: String,
    val backImage: String,
    val viewCount: Int,
    val profileImage: String
)
data class TodayClosetUploadRequest(
    val postId: Int
)


data class TodayClosetUploadResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TodayClosetUploadResult
)

data class TodayClosetUploadResult(
    val todayClosetId: Int,
    val createdAt: String
)