package com.example.umc_closit.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.umc_closit.data.remote.timeline.PostPreview
import com.example.umc_closit.data.remote.timeline.TimelineResponse
import com.example.umc_closit.utils.TokenUtils

class TimelineViewModel : ViewModel() {
    private val _timelineItems = MutableLiveData<List<PostPreview>?>()
    val timelineItems: MutableLiveData<List<PostPreview>?> get() = _timelineItems

    fun fetchTimelinePosts(token: String, userId: Int, page: Int = 0, hashtag: String? = null, context: android.content.Context) {
        val apiCall = {
            com.example.umc_closit.data.remote.RetrofitClient.timelineService.getPosts(
                token = "Bearer $token",
                userId = userId,
                hashtag = hashtag,
                page = page,
                size = 10
            )
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response: TimelineResponse ->
                Log.d("TIMELINE_RESPONSE", "응답 본문: $response")
                if (response.isSuccess) {
                    _timelineItems.value = response.result.postPreviewList.filterNotNull()

                }
            },
            onFailure = { t ->
                Log.e("TIMELINE_ERROR", "네트워크 오류: ${t.message}")
            },
            retryCall = apiCall,
            context = context

        )
    }
}
