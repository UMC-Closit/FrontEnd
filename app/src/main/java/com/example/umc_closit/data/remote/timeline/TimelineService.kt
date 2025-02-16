package com.example.umc_closit.data.remote.timeline

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TimelineService {

    @GET("api/auth/posts")
    fun getPosts(
        @Header("Authorization") token: String,
        @Query("follower") follower: Boolean = false,
        @Query("user_id") userId: Int?,
        @Query("hashtag") hashtag: String?,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Call<TimelineResponse>

    @POST("/api/auth/posts/{post_id}/likes")
    fun likePost(
        @Header("Authorization") token: String,
        @Path("post_id") postId: Int,
        @Query("user_id") userId: Int
    ): Call<LikeResponse>

    @POST("api/auth/bookmarks")
    fun savePost(
        @Header("Authorization") token: String,
        @Body request: BookmarkRequest
    ): Call<BookmarkResponse>

    @PATCH("/api/auth/notifications")
    fun getNotifications(
        @Header("Authorization") token: String,
        @Query("page") page: Int
    ): Call<NotificationResponse>

    @PATCH("/api/auth/notifications/{notification_id}")
    fun readNotification(
        @Header("Authorization") token: String,
        @Path("notification_id") notificationId: Int
    ): Call<NotificationReadResponse>

    @DELETE("/api/auth/notifications/{notification_id}")
    fun deleteNotification(
        @Header("Authorization") token: String,
        @Path("notification_id") notificationId: Int
    ): Call<NotificationDeleteResponse>

}
