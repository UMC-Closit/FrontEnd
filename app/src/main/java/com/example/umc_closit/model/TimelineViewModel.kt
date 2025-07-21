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
        Log.d("TIMELINE_DEBUG", "🔄 타임라인 데이터 요청 시작 - 페이지: $currentPage")

        val apiCall = {
            Log.d("TimelineViewModel", "📡 API 요청 준비 완료 - page=$currentPage")
            RetrofitClient.timelineService.getPosts(page = currentPage, size = 10)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->

                Log.d("TIMELINE_DEBUG", "✅ 타임라인 API 응답 성공")
                Log.d("TIMELINE_DEBUG", "📊 응답 데이터: $response")
                Log.d("TIMELINE_DEBUG", "📋 isSuccess: ${response.isSuccess}")
                Log.d("TIMELINE_DEBUG", "📋 code: ${response.code}")
                Log.d("TIMELINE_DEBUG", "📋 message: ${response.message}")
                
                if (response.isSuccess) {
                    Log.d("TIMELINE_DEBUG", "📋 result.postPreviewList 크기: ${response.result.postPreviewList.size}")
                    Log.d("TIMELINE_DEBUG", "📋 result.hasNext: ${response.result.hasNext}")
                    Log.d("TIMELINE_DEBUG", "📋 result.listSize: ${response.result.listSize}")
                    
                    // 각 아이템의 데이터 확인
                    response.result.postPreviewList.forEachIndexed { index, item ->
                        Log.d("TIMELINE_DEBUG", "📋 아이템[$index] postId: ${item.postId}")
                        Log.d("TIMELINE_DEBUG", "📋 아이템[$index] clositId: ${item.clositId}")
                        Log.d("TIMELINE_DEBUG", "📋 아이템[$index] frontImage: ${item.frontImage}")
                        Log.d("TIMELINE_DEBUG", "📋 아이템[$index] backImage: ${item.backImage}")
                    }
                    
                    val newItems = response.result.postPreviewList.filterNotNull()
                    Log.d("TIMELINE_DEBUG", "📋 필터링된 아이템 수: ${newItems.size}")
                    
                    val currentList = _timelineItems.value.orEmpty().toMutableList()
                    currentList.addAll(newItems)
                    _timelineItems.value = currentList

                    hasNextPage = response.result?.hasNext ?: false
                    Log.d("TimelineViewModel", "📄 hasNextPage: $hasNextPage")
                    currentPage++
                    
                    Log.d("TIMELINE_DEBUG", "📋 현재 총 아이템 수: ${currentList.size}")
                    Log.d("TIMELINE_DEBUG", "📋 다음 페이지 존재: $hasNextPage")
                    Log.d("TIMELINE_DEBUG", "📋 현재 페이지: $currentPage")
                } else {
                    Log.e("TIMELINE_DEBUG", "❌ API 응답 실패: ${response.message}")
                }

                _isLoading.value = false
            },
            onFailure = { error ->
                Log.e("TIMELINE_DEBUG", "❌ 타임라인 API 요청 실패: ${error.message}")
                Log.e("TIMELINE_DEBUG", "❌ 에러 상세: ", error)
                _isLoading.value = false
            },
            context = context
        )
    }

    fun resetPage() {
        Log.d("TIMELINE_DEBUG", "🔄 페이지 초기화")
        currentPage = 0
        hasNextPage = true
        _timelineItems.value = emptyList()
    }
}
