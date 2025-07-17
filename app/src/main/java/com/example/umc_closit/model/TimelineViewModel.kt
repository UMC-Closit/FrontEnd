package com.example.umc_closit.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.timeline.PostPreview
import com.example.umc_closit.data.remote.timeline.TimelineResponse
import com.example.umc_closit.utils.TokenUtils

class TimelineViewModel : ViewModel() {
    private val _timelineItems = MutableLiveData<List<PostPreview>?>()
    val timelineItems: LiveData<List<PostPreview>?> get() = _timelineItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    var currentPage = 0
    var hasNextPage = true

    fun fetchTimelinePosts(context: Context) {
        if (_isLoading.value == true || !hasNextPage) {
            Log.d("TimelineViewModel", "⛔ 요청 차단됨: isLoading=${_isLoading.value}, hasNextPage=$hasNextPage")
            return
        }

        Log.d("TimelineViewModel", "🚀 fetchTimelinePosts 시작 - currentPage=$currentPage")
        _isLoading.value = true

        val apiCall = {
            Log.d("TimelineViewModel", "📡 API 요청 준비 완료 - page=$currentPage")
            RetrofitClient.timelineService.getPosts(page = currentPage, size = 10)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                Log.d("TimelineViewModel", "✅ 응답 도착 - isSuccess=${response.isSuccess}")

                if (response.isSuccess) {
                    val newItems = response.result?.postPreviewList?.filterNotNull() ?: emptyList()
                    Log.d("TimelineViewModel", "📦 새로 가져온 아이템 수: ${newItems.size}")

                    val currentList = _timelineItems.value.orEmpty().toMutableList()
                    currentList.addAll(newItems)
                    _timelineItems.value = currentList

                    hasNextPage = response.result?.hasNext ?: false
                    Log.d("TimelineViewModel", "📄 hasNextPage: $hasNextPage")
                    currentPage++
                    Log.d("TimelineViewModel", "➡️ 다음 페이지로 이동: currentPage=$currentPage")
                } else {
                    Log.w("TimelineViewModel", "❌ 응답 실패: ${response.message}")
                }

                _isLoading.value = false
            },
            onFailure = { t ->
                Log.e("TimelineViewModel", "🔥 API 호출 실패: ${t.message}", t)
                _isLoading.value = false
            },
            context = context
        )
    }

    fun resetPage() {
        currentPage = 0
        hasNextPage = true
        _timelineItems.value = emptyList()
    }
}
