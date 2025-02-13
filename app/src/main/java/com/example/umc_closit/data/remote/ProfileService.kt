package com.example.umc_closit.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileService {

    @POST("/api/auth/follows")
    fun followUser(
        @Body request: FollowRequest
    ): Call<FollowResponse>

    @DELETE("/api/auth/follows/{followerId}/{followingId}")
    fun unfollowUser(
        @Path("followerId") followId: Int,
        @Path("followingId") followingId: Int
    ): Call<UnfollowResponse>

}
