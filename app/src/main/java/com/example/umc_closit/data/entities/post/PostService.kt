package com.example.umc_closit.data.entities.post

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {
    @GET("/api/auth/posts/{post_id}")
    fun getPost(
        @Path("post_id") postId: Long
    ): Call<PostResponse>

    @DELETE("/api/auth/posts/{post_id}")
    fun deletePost(
        @Path("post_id") postId: Long
    ): Call<DeletePostResponse>

    @POST("/api/auth/posts")
    fun uploadPost(
        @Body request: PostRequest
    ): Call<PostUploadResponse>
}

