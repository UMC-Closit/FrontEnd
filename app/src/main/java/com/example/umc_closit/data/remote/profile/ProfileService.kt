package com.example.umc_closit.data.remote.profile

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileService {

    @GET("api/auth/users/{closit_id}")
    fun getUserProfile(
        @Header("Authorization") token: String,
        @Path("closit_id") clositId: String
    ): Call<ProfileUserResponse>

    @POST("/api/auth/follows")
    fun followUser(
        @Header("Authorization") token: String,
        @Body request: FollowRequest
    ): Call<FollowResponse>

    @DELETE("/api/auth/follows/{followerId}/{followingId}")
    fun unfollowUser(
        @Header("Authorization") token: String,
        @Path("followerId") followId: String,
        @Path("followingId") followingId: String
    ): Call<UnfollowResponse>

}
