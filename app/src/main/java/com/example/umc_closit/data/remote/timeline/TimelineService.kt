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
    @GET("api/v1/posts")
    fun getPosts(
        @Query("follower") follower: Boolean = false,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "LATEST"
    ): Call<TimelineResponse>

    // like
    @POST("/api/v1/posts/{post_id}/likes")
    fun addLike(
        @Path("post_id") postId: Int
    ): Call<LikeResponse>

    @DELETE("/api/v1/posts/{post_id}/likes")
    fun removeLike(
        @Path("post_id") postId: Int
    ): Call<LikeResponse>

    // bookmark
    @POST("/api/v1/bookmarks")
    fun addBookmark(
        @Body request: BookmarkRequest
    ): Call<BookmarkCreateResponse>

    @DELETE("/api/v1/bookmarks/{post_id}")
    fun removeBookmark(
        @Path("post_id") postId: Int
    ): Call<BookmarkDeleteResponse>

    // notification
    @PATCH("/api/v1/notifications")
    fun getNotifications(
        @Query("page") page: Int
    ): Call<NotificationResponse>


    @PATCH("/api/v1/notifications/{notification_id}")
    fun readNotification(
        @Path("notification_id") notificationId: Int
    ): Call<NotificationReadResponse>

    @DELETE("/api/v1/notifications/{notification_id}")
    fun deleteNotification(
        @Path("notification_id") notificationId: Int
    ): Call<NotificationDeleteResponse>

    // comments
    @GET("/api/v1/posts/{post_id}/comments")
    fun getComments(
        @Path("post_id") postId: Int,
        @Query("page") page: Int
    ): Call<CommentListResponse>

    @POST("/api/v1/posts/{post_id}/comments")
    fun postComment(
        @Path("post_id") postId: Int,
        @Body content: CommentRequest
    ): Call<CommentCreateResponse>

    @DELETE("/api/v1/posts/{post_id}/comments/{comment_id}")
    fun deleteComment(
        @Path("post_id") postId: Int,
        @Path("comment_id") commentId: Int
    ): Call<CommentDeleteResponse>

}
