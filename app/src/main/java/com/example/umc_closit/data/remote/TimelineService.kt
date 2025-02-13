package com.example.umc_closit.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TimelineService {
    @GET("api/auth/posts")
    fun getPosts(
        @Query("follower") follower: Boolean = false,
        @Query("user_id") userId: Int?,
        @Query("hashtag") hashtag: String?,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Call<TimelineResponse>

    @POST("/api/auth/posts/{post_id}/likes")
    fun likePost(
        //@Header("Authorization") token: String, // Access Token
        @Path("post_id") postId: Int,
        @Query("user_id") userId: Int
    ): Call<LikeResponse>

    @POST("api/auth/bookmarks")
    fun savePost(
        @Body request: BookmarkRequest
    ): Call<BookmarkResponse>
}