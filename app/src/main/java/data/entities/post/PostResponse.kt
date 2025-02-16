package com.example.umc_closit.data.entities.post

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: PostResult
) : Parcelable

@Parcelize
data class PostResult(
    val postId: Long,
    val userId: Long,
    val profileImage: String,
    val frontImage: String,
    val backImage: String,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val isFriend: Boolean,
    val hashtags: List<String>,
    val frontItemtags: List<ItemTag>,
    val backItemtags: List<ItemTag>,
    val pointColor: String,
    val visibility: String,
    val mission: Boolean
) : Parcelable

@Parcelize
data class ItemTag(
    val x: Int,
    val y: Int,
    val content: String
) : Parcelable

