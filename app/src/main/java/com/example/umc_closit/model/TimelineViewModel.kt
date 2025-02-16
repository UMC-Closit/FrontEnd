package com.example.umc_closit.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.umc_closit.data.remote.timeline.PostPreview
import com.example.umc_closit.data.remote.timeline.TimelineResponse
import com.example.umc_closit.utils.TokenUtils

class TimelineViewModel : ViewModel() {
    private val _timelineItems = MutableLiveData<List<PostPreview>?>().apply { value = null }
    val timelineItems: MutableLiveData<List<PostPreview>?> get() = _timelineItems

    var currentPage = 0
    var isLoading = false
    var hasNextPage = true

    fun fetchTimelinePosts(
        token: String,
        userId: Int,
        hashtag: String? = null,
        context: android.content.Context
    ) {
        if (isLoading || !hasNextPage) return

        isLoading = true

        val apiCall = {
            com.example.umc_closit.data.remote.RetrofitClient.timelineService.getPosts(
                token = "Bearer $token",
                userId = userId,
                hashtag = hashtag,
                page = currentPage,
                size = 10
            )
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response: TimelineResponse ->
                Log.d("TIMELINE_RESPONSE", "응답 본문: $response")
                if (response.isSuccess) {
                    val newItems = response.result.postPreviewList.filterNotNull()

                    val currentList = _timelineItems.value?.toMutableList() ?: mutableListOf()
                    currentList.addAll(newItems)

                    _timelineItems.value = currentList

                    hasNextPage = response.result.hasNext
                    currentPage++
                }
                isLoading = false
            },
            onFailure = { t ->
                Log.e("TIMELINE_ERROR", "네트워크 오류: ${t.message}")
                isLoading = false
            },
            retryCall = apiCall,
            context = context
        )
    }
}
