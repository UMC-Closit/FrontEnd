package com.example.umc_closit.data

import BattleComment
import CommentPostResult
import CommentRequest
import CommentResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.umc_closit.data.remote.BaseResponse
import com.example.umc_closit.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            .enqueue(object : Callback<BaseResponse<CommentResult>> {
                override fun onResponse(
                    call: Call<BaseResponse<CommentResult>>,
                    response: Response<BaseResponse<CommentResult>>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        _comments.value = response.body()?.result?.battleCommentPreviewList ?: emptyList()
                    } else {
                        _errorMessage.value = "댓글 불러오기 실패: ${response.body()?.message}"
                    }
                }

                override fun onFailure(call: Call<BaseResponse<CommentResult>>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "네트워크 오류: ${t.message}"
                }
            })
    }

    // 댓글 작성 메서드
    fun postComment(battleId: Long, content: String, parentCommentId: Long? = null) {
        val commentRequest = CommentRequest(content, parentCommentId ?: 0L)

        battleApiService.postBattleComment(battleId, commentRequest)
            .enqueue(object : Callback<BaseResponse<CommentPostResult>> {
                override fun onResponse(
                    call: Call<BaseResponse<CommentPostResult>>,
                    response: Response<BaseResponse<CommentPostResult>>
                ) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        _isCommentPosted.value = true
                        fetchComments(battleId, page = 0)
                    } else {
                        _isCommentPosted.value = false
                        _errorMessage.value = "댓글 작성 실패: ${response.body()?.message}"
                    }
                }

                override fun onFailure(call: Call<BaseResponse<CommentPostResult>>, t: Throwable) {
                    _isCommentPosted.value = false
                    _errorMessage.value = "네트워크 오류: ${t.message}"
                }
            })
    }

    // 댓글 삭제 메서드
    fun deleteComment(battleId: Long, commentId: Int) {
        _isLoading.value = true

        battleApiService.deleteBattleComment(battleId, commentId)
            .enqueue(object : Callback<BaseResponse<String>> {
                override fun onResponse(
                    call: Call<BaseResponse<String>>,
                    response: Response<BaseResponse<String>>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        _isCommentDeleted.value = true
                        fetchComments(battleId, page = 0)
                    } else {
                        _isCommentDeleted.value = false
                        _errorMessage.value = "댓글 삭제 실패: ${response.body()?.message}"
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String>>, t: Throwable) {
                    _isLoading.value = false
                    _isCommentDeleted.value = false
                    _errorMessage.value = "네트워크 오류: ${t.message}"
                }
            })
    }
}
