package com.example.umc_closit.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

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
    val viewCount: Int
)

// 오늘의 옷장 API 인터페이스
interface TodayClosetApiService {
    @GET("/api/auth/communities/todayclosets")
    fun getTodayClosets(
        @Query("page") page: Int
    ): Call<TodayClosetResponse>
}
