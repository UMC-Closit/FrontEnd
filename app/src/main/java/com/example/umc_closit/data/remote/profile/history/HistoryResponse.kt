package com.example.umc_closit.data.remote.profile.history

// history
data class DateHistoryResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: DateHistoryResult
)

data class DateHistoryResult(
    val dateHistoryThumbnailDTOList: List<DateHistoryThumbnail>?,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)


data class DateHistoryThumbnail(
    val postId: Int,
    val thumbnail: String,
    val createdAt: String
)

//color
data class ColorHistoryResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ColorHistoryResult
)

data class ColorHistoryResult(
    val colorHistoryThumbnailDTOList: List<ColorHistoryThumbnail>,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)

data class ColorHistoryThumbnail(
    val postId: Int,
    val thumbnail: String,
    val createdAt: String
)

// detail
data class HistoryDetailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: HistoryDetailResult
)

data class HistoryDetailResult(
    val postList: List<HistoryDetailPost>,
    val date: String
)

data class HistoryDetailPost(
    val postId: Int,
    val createdAt: String
)
