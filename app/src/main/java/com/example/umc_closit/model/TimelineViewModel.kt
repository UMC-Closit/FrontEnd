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
        if (_isLoading.value == true || !hasNextPage) return

        _isLoading.value = true

        val apiCall = {
            RetrofitClient.timelineService.getPosts(page = currentPage, size = 10)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    val newItems = response.result.postPreviewList.filterNotNull()
                    val currentList = _timelineItems.value.orEmpty().toMutableList()
                    currentList.addAll(newItems)
                    _timelineItems.value = currentList

                    hasNextPage = response.result.hasNext
                    currentPage++
                }
                _isLoading.value = false
            },
            onFailure = {
                _isLoading.value = false
            },
            retryCall = apiCall,
            context = context
        )
    }

    fun resetPage() {
        currentPage = 0
        hasNextPage = true
        _timelineItems.value = emptyList()
    }
}
