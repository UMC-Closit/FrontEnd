package com.example.umc_closit.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.PostPreview
import com.example.umc_closit.data.remote.TimelineResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimelineViewModel : ViewModel() {
    private val _timelineItems = MutableLiveData<List<PostPreview>?>()
    val timelineItems: MutableLiveData<List<PostPreview>?> get() = _timelineItems

    fun fetchTimelinePosts(token: String, page: Int = 0) {
        RetrofitClient.timelineService.getTimelinePosts("Bearer $token", page = page)
            .enqueue(object : Callback<TimelineResponse> {
                override fun onResponse(call: Call<TimelineResponse>, response: Response<TimelineResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.isSuccess) {
                                _timelineItems.value = it.result.postPreviewList
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TimelineResponse>, t: Throwable) {
                    Log.e("TIMELINE_ERROR", "네트워크 오류: ${t.message}")
                }
            })
    }

    // 좋아요 상태 변경 (API 호출 없이 로컬에서 변경)
    fun toggleLike(postId: Int) {
        val updatedItems = _timelineItems.value?.map {
            if (it.userId == postId) {
                it.copy(isLiked = !it.isLiked)
            } else it
        }
        _timelineItems.value = updatedItems
    }

    // 저장 상태 변경 (API 호출 없이 로컬에서 변경)
    fun toggleSave(postId: Int) {
        val updatedItems = _timelineItems.value?.map {
            if (it.userId == postId) {
                it.copy(isSaved = !it.isSaved)
            } else it
        }
        _timelineItems.value = updatedItems
    }

    // 게시글 상태 가져오기
    fun getPostStatus(postId: Int): Pair<Boolean, Boolean>? {
        return _timelineItems.value?.find { it.userId == postId }?.let {
            Pair(it.isLiked, it.isSaved)
        }
    }

    // 게시글 상태 업데이트
    fun updatePostStatus(postId: Int, isLiked: Boolean, isSaved: Boolean) {
        val updatedItems = _timelineItems.value?.map {
            if (it.userId == postId) {
                it.copy(isLiked = isLiked, isSaved = isSaved)
            } else it
        }
        _timelineItems.value = updatedItems
    }
}
