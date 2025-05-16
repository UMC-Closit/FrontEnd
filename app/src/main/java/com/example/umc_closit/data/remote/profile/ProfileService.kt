package com.example.umc_closit.data.remote.profile

import com.example.umc_closit.data.remote.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @PATCH("/api/auth/users/profile-image")
    fun uploadProfileImage(
        @Body request: RequestBody
    ): Call<ProfileImageUploadResponse>

    @PATCH("/api/auth/users/")
    fun updateUserProfile(
        @Body request: EditProfileRequest
    ): Call<ProfileUserResponse>

    @POST("/api/auth/users/profile-image/presigned-url")
    fun getPresignedProfileUrl(@Body body: RequestBody): Call<PresignedProfileUrlResponse>

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

    // following list
    @GET("/api/auth/users/{closit_id}/following")
    fun getFollowingList(
        @Path("closit_id") clositId: String,  // 사용자 ID
        @Query("page") page: Int,  // 페이지 번호
        @Query("size") size: Int   // 한 페이지에 불러올 항목 수
    ): Call<FollowingResponse>

    // follower list
    @GET("/api/auth/users/{closit_id}/followers")
    fun getFollowersList(
        @Path("closit_id") clositId: String,  // 사용자 ID
        @Query("page") page: Int,  // 페이지 번호
        @Query("size") size: Int   // 한 페이지에 불러올 항목 수
    ): Call<FollowerResponse>

    // block
    @GET("/api/auth/users/block")
    fun getBlockedUsers(): Call<BlockedUserListResponse>

    @POST("/api/auth/users/block")
    fun blockUser(@Body body: BlockRequest): Call<BaseResponse<String>>

    @DELETE("/api/auth/users/block")
    fun unblockUser(@Body body: BlockRequest): Call<BaseResponse<String>>

}
