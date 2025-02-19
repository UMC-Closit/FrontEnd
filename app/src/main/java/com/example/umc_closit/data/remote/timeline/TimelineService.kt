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

    // timeline
    @GET("api/auth/posts")
    fun getPosts(
        @Query("follower") follower: Boolean = false,
        @Query("user_id") userId: Int?,
        @Query("hashtag") hashtag: String?,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Call<TimelineResponse>

    // like
    @POST("/api/auth/posts/{post_id}/likes")
    fun addLike(
        @Path("post_id") postId: Int
    ): Call<LikeResponse>

    @DELETE("/api/auth/posts/{post_id}/likes")
    fun removeLike(
        @Path("post_id") postId: Int
    ): Call<LikeResponse>

    // bookmark
    @POST("/api/auth/bookmarks")
    fun addBookmark(
        @Body request: BookmarkRequest
    ): Call<BookmarkCreateResponse>

    @DELETE("/api/auth/bookmarks/{post_id}")
    fun removeBookmark(
        @Path("post_id") postId: Int
    ): Call<BookmarkDeleteResponse>

    // notification
    @PATCH("/api/auth/notifications")
    fun getNotifications(
        @Query("page") page: Int
    ): Call<NotificationResponse>


    @PATCH("/api/auth/notifications/{notification_id}")
    fun readNotification(
        @Path("notification_id") notificationId: Int
    ): Call<NotificationReadResponse>

    @DELETE("/api/auth/notifications/{notification_id}")
    fun deleteNotification(
        @Path("notification_id") notificationId: Int
    ): Call<NotificationDeleteResponse>

    // comments
    @GET("/api/auth/posts/{post_id}/comments")
    fun getComments(
        @Path("post_id") postId: Int,
        @Query("page") page: Int
    ): Call<CommentListResponse>

    @POST("/api/auth/posts/{post_id}/comments")
    fun postComment(
        @Path("post_id") postId: Int,
        @Body content: CommentRequest
    ): Call<CommentCreateResponse>

    @DELETE("/api/auth/posts/{post_id}/comments/{comment_id}")
    fun deleteComment(
        @Path("post_id") postId: Int,
        @Path("comment_id") commentId: Int
    ): Call<CommentDeleteResponse>

}
