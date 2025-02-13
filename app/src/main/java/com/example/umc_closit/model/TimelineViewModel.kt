package com.example.umc_closit.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.umc_closit.data.remote.PostPreview
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.TimelineResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimelineViewModel : ViewModel() {
    private val _timelineItems = MutableLiveData<List<PostPreview>?>()
    val timelineItems: MutableLiveData<List<PostPreview>?> get() = _timelineItems

    fun fetchTimelinePosts(userId: Int, page: Int = 0, hashtag: String? = null) {
        RetrofitClient.timelineService.getPosts(
            userId = userId,
            hashtag = hashtag,
            page = page,
            size = 10 // 필요하면 변경
        ).enqueue(object : Callback<TimelineResponse> {
            override fun onResponse(call: Call<TimelineResponse>, response: Response<TimelineResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isSuccess) {
                            _timelineItems.value = it.result.postPreviewList
                        }
                    }
                } else {
                    Log.e("TIMELINE_ERROR", "응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TimelineResponse>, t: Throwable) {
                Log.e("TIMELINE_ERROR", "네트워크 오류: ${t.message}")
            }
        })
    }
}
