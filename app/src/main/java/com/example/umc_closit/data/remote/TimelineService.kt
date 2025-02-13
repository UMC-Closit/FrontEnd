package com.example.umc_closit.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TimelineService {
    @GET("/api/auth/posts")
    fun getTimelinePosts(
        @Header("Authorization") token: String, // 액세스 토큰
        @Query("follower") follower: Boolean = false, // 팔로워 게시글 여부
        @Query("hashtag_id") hashtagId: Int? = null, // 해시태그 ID (선택)
        @Query("page") page: Int = 0 // 페이지 번호
    ): Call<TimelineResponse>

    @POST("/api/auth/posts/{post_id}/likes")
    fun likePost(
        @Header("Authorization") token: String, // Access Token
        @Path("post_id") postId: Int,
        @Query("user_id") userId: Int
    ): Call<LikeResponse>
}