package com.example.umc_closit.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.umc_closit.data.entities.BattleItem
import com.example.umc_closit.data.remote.battle.BattleComment
import com.example.umc_closit.data.remote.battle.CommentResponse
import com.example.umc_closit.data.remote.battle.CommentRequest
import com.example.umc_closit.data.remote.battle.CommentPostResponse
import com.example.umc_closit.data.remote.battle.DeleteCommentResponse
import com.example.umc_closit.data.remote.RetrofitClient
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// BattleViewModel: 배틀 관련 상태 관리를 담당하는 ViewModel입니다.
class BattleViewModel: ViewModel() {
    // 좋아요 상태 관리
    private val likedPosts = mutableMapOf<Int, Boolean>()

    fun getLikeStatus(postId: Int): Boolean? = likedPosts[postId]

    fun updateLikeStatus(postId: Int, isLiked: Boolean) {
        likedPosts[postId] = isLiked
    }

    // 댓글 리스트 LiveData
    private val _comments = MutableLiveData<List<BattleComment>>()
    val comments: LiveData<List<BattleComment>> get() = _comments

    // 댓글 작성 성공 여부
    private val _isCommentPosted = MutableLiveData<Boolean>()
    val isCommentPosted: LiveData<Boolean> get() = _isCommentPosted

    // 댓글 삭제 성공 여부
    private val _isCommentDeleted = MutableLiveData<Boolean>()
    val isCommentDeleted: LiveData<Boolean> get() = _isCommentDeleted

    // 로딩 상태
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 에러 메시지
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val battleApiService = RetrofitClient.battleApiService

    // 댓글 조회 메서드
    fun fetchComments(battleId: Long, page: Int) {
        _isLoading.value = true

        battleApiService.getBattleComments(battleId, page)
            .enqueue(object : Callback<CommentResponse> {
                override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        _comments.value = response.body()?.result?.battleCommentPreviewList ?: emptyList()
                    } else {
                        _errorMessage.value = "댓글 불러오기 실패: ${response.body()?.message}"
                    }
                }

                override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "네트워크 오류: ${t.message}"
                }
            })
    }

    // 댓글 작성 메서드
    fun postComment(battleId: Long, content: String) {
        val commentRequest = CommentRequest(content)

        battleApiService.postBattleComment(battleId, commentRequest)
            .enqueue(object : Callback<CommentPostResponse> {
                override fun onResponse(call: Call<CommentPostResponse>, response: Response<CommentPostResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        _isCommentPosted.value = true
                        fetchComments(battleId, page = 0) // 댓글 작성 후 리스트 새로고침
                    } else {
                        _isCommentPosted.value = false
                        _errorMessage.value = "댓글 작성 실패: ${response.body()?.message}"
                    }
                }

                override fun onFailure(call: Call<CommentPostResponse>, t: Throwable) {
                    _isCommentPosted.value = false
                    _errorMessage.value = "네트워크 오류: ${t.message}"
                }
            })
    }

    // 댓글 삭제 메서드
    fun deleteComment(battleId: Long, commentId: Int) {
        _isLoading.value = true

        battleApiService.deleteBattleComment(battleId, commentId)
            .enqueue(object : Callback<DeleteCommentResponse> {
                override fun onResponse(
                    call: Call<DeleteCommentResponse>,
                    response: Response<DeleteCommentResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        _isCommentDeleted.value = true
                        fetchComments(battleId, page = 0) // 댓글 리스트 새로고침
                    } else {
                        _isCommentDeleted.value = false
                        _errorMessage.value = "댓글 삭제 실패: ${response.body()?.message}"
                    }
                }

                override fun onFailure(call: Call<DeleteCommentResponse>, t: Throwable) {
                    _isLoading.value = false
                    _isCommentDeleted.value = false
                    _errorMessage.value = "네트워크 오류: ${t.message}"
                }
            })
    }

}
