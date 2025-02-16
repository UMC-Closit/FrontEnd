package com.example.umc_closit.data.entities.post

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostUploadResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: UploadResult
) : Parcelable

@Parcelize
data class UploadResult(
    val clositId: String,
    val postId: Int,
    val createdAt: String,
    val visibility: String
) : Parcelable
