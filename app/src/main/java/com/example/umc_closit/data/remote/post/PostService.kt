package com.example.umc_closit.data.remote.post

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path

interface PostService {
    @GET("/api/auth/posts/{post_id}")
    suspend fun getPostDetail(
        @Path("post_id") postId: Int
    ): Response<PostResponse>

    @DELETE("/api/auth/posts/{post_id}")
    suspend fun deletePost(
        @Path("post_id") postId: Int
    ): Response<PostDeleteResponse>

    @Multipart
    @POST("/api/auth/posts")
    suspend fun uploadPost(
        @Part("request") request: RequestBody,
        @Part frontImage: MultipartBody.Part,
        @Part backImage: MultipartBody.Part
    ): Response<PostUploadResponse>
}

