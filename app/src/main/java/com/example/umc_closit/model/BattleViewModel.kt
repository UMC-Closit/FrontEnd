package com.example.umc_closit.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.umc_closit.data.entities.BattleItem

// BattleViewModel: 배틀 관련 상태 관리를 담당하는 ViewModel입니다.
class BattleViewModel: ViewModel() {
    // 좋아요 상태 관리
    private val likedPosts = mutableMapOf<Long, Boolean>()

    fun getLikeStatus(postId: Long): Boolean? = likedPosts[postId]

    fun updateLikeStatus(postId: Long, isLiked: Boolean) {
        likedPosts[postId] = isLiked
    }

}
