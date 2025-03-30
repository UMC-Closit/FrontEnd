package com.example.umc_closit.data.remote.profile

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileService {

    // follow
    @POST("/api/auth/follows")
    fun followUser(
        @Body request: FollowRequest
    ): Call<FollowResponse>

    @DELETE("/api/auth/follows/{receiver_closit_id}")
    fun unfollowUser(
        @Path("receiver_closit_id") receiverClositId: String
    ): Call<UnfollowResponse>

    // check follow
    @GET("/api/auth/follows/{receiver_closit_id}")
    fun checkFollowStatus(
        @Path("receiver_closit_id") receiverClositId: String
    ): Call<FollowCheckResponse>

    // profile info
    @GET("/api/auth/users/{closit_id}")
    fun getUserProfile(
        @Path("closit_id") clositId: String
    ): Call<ProfileUserResponse>

    @Multipart
    @PATCH("/api/auth/users/{closit_id}/profile-image")
    fun uploadProfileImage(
        @Path("closit_id") clositId: String,
        @Part user_image: MultipartBody.Part
    ): Call<ProfileUserResponse>

    @PATCH("/api/auth/users/")
    fun updateUserProfile(
        @Body request: EditProfileRequest
    ): Call<ProfileUserResponse>

    // highlight
    @GET("/api/auth/users/{closit_id}/highlights")
    fun getHighlights(
        @Path("closit_id") clositId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Call<HighlightListResponse>

    @GET("/api/auth/highlights/{highlight_id}")
    fun getHighlightDetail(
        @Path("highlight_id") highlightId: Int
    ): Call<HighlightDetailResponse>

    @POST("/api/auth/highlights")
    fun createHighlight(
        @Body postId: Map<String, Int>
    ): Call<HighlightCreateResponse>

    @DELETE("/api/auth/highlights/{highlight_id}")
    fun deleteHighlight(
        @Path("highlight_id") highlightId: Int
    ): Call<HighlightDeleteResponse>

    @GET("/api/auth/bookmarks")
    fun getBookmarks(
        @Query("page") page: Int = 0,  // 기본값 0
        @Query("size") size: Int = 10  // 기본값 10
    ): Call<BookmarkResponse>

}
