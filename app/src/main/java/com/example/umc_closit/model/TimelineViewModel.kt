package com.example.umc_closit.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.umc_closit.R
import com.example.umc_closit.data.TimelineItem

class TimelineViewModel : ViewModel() {
    private val _timelineItems = MutableLiveData<List<TimelineItem>>()
    val timelineItems: LiveData<List<TimelineItem>> get() = _timelineItems

    init {
        // 초기 데이터 설정
        _timelineItems.value = listOf(
            TimelineItem(
                id = 1,
                mainImageResId = R.drawable.img_timeline_big_default,
                overlayImageResId = R.drawable.img_timeline_small_default,
                userProfileResId = R.drawable.img_profile_default,
                userName = "User1",
                likeCount = 10,
                commentCount = 5,
                isLiked = false,
                isSaved = true,
                postText = "This is a sample post text",
                hashtags = listOf("#Nature", "#Sunset")
            ),
            TimelineItem(
                id = 2,
                mainImageResId = R.drawable.example_profile,
                overlayImageResId = R.drawable.example_profile,
                userProfileResId = R.drawable.example_profile,
                userName = "User2",
                likeCount = 25,
                commentCount = 3,
                isLiked = true,
                isSaved = false,
                postText = "Another sample post",
                hashtags = listOf("#Travel", "#Adventure")
            )
        )
    }

    // 좋아요 상태 변경
    fun toggleLike(postId: Int) {
        val updatedItems = _timelineItems.value?.map {
            if (it.id == postId) {
                it.copy(isLiked = !it.isLiked, likeCount = if (it.isLiked) it.likeCount - 1 else it.likeCount + 1)
            } else it
        }
        _timelineItems.value = updatedItems
    }

    // 저장 상태 변경
    fun toggleSave(postId: Int) {
        val updatedItems = _timelineItems.value?.map {
            if (it.id == postId) {
                it.copy(isSaved = !it.isSaved)
            } else it
        }
        _timelineItems.value = updatedItems
    }

    // 게시글 상태 가져오기
    fun getPostStatus(postId: Int): Pair<Boolean, Boolean>? {
        return _timelineItems.value?.find { it.id == postId }?.let {
            Pair(it.isLiked, it.isSaved)
        }
    }

    // 게시글 상태 업데이트
    fun updatePostStatus(postId: Int, isLiked: Boolean, isSaved: Boolean) {
        val updatedItems = _timelineItems.value?.map {
            if (it.id == postId) {
                it.copy(isLiked = isLiked, isSaved = isSaved)
            } else it
        }
        _timelineItems.value = updatedItems
    }
}