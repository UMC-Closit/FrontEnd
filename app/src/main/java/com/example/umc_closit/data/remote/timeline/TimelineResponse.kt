package com.example.umc_closit.data.remote.timeline

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class TimelineResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TimelineResult
)

data class TimelineResult(
    val postPreviewList: List<PostPreview>,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)

// Timeline item
@Parcelize
data class PostPreview(
    val postId: Int,
    val userId: Int,
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
    val visibility: String
)
 : Parcelable

@Parcelize
data class ItemTag(
    val x: Float,
    val y: Float
) : Parcelable

// like
data class LikeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LikeResult
)

data class LikeResult(
    val isLiked: Boolean,
    val postId: Int,
    val userId: Int
)

// save
data class BookmarkRequest(
    val userId: Int,
    val postId: Int
)

data class BookmarkResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: BookmarkResult
)

data class BookmarkResult(
    val bookmarkId: Int,
    val postId: Int,
    val userId: Int
)

// notification
data class NotificationResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: NotificationResult
)

data class NotificationResult(
    val notiPreviewDTOList: List<NotificationItem>,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)

data class NotificationItem(
    val notificationId: Int,
    val clositId: String,
    val userName: String,
    val imageUrl: String,
    val content: String,
    val type: String,
    val read: Boolean
)

data class NotificationReadResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: NotificationItem
)

data class NotificationDeleteResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String
)
