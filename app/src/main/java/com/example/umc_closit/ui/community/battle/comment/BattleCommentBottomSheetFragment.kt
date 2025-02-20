package com.example.umc_closit.ui.battle.comment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.battle.BattleComment
import com.example.umc_closit.data.remote.battle.CommentRequest
import com.example.umc_closit.databinding.FragmentCommentBottomSheetBinding
import com.example.umc_closit.utils.TokenUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BattleCommentBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCommentBottomSheetBinding
    private lateinit var commentAdapter: BattleCommentAdapter
    private val comments = mutableListOf<BattleComment>()

    private var battleId: Long = -1
    private var page: Int = 0
    private var hasNext = true

    private val handler = Handler(Looper.getMainLooper())
    private val timeUpdateRunnable = object : Runnable {
        override fun run() {
            commentAdapter.updateTimeForAllItems()
            handler.postDelayed(this, 10000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        battleId = arguments?.getLong("battleId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentBottomSheetBinding.inflate(inflater, container, false)

        commentAdapter = BattleCommentAdapter(comments, ::deleteComment)
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.commentsRecyclerView.adapter = commentAdapter

        val myClositId = TokenUtils.getClositId(requireContext()) ?: ""
        val itemTouchHelper = ItemTouchHelper(BattleCommentSwipeCallback(commentAdapter, myClositId))
        itemTouchHelper.attachToRecyclerView(binding.commentsRecyclerView)

        loadComments()

        binding.ivSubmit.setOnClickListener { postComment() }

        binding.commentsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1 && hasNext) {
                    loadComments()
                }
            }
        })

        return binding.root
    }

    private var isLoading = false

    private fun loadComments() {
        if (!hasNext || isLoading) return
        isLoading = true

        Log.d("BATTLE_COMMENT_LOAD", "댓글 로드 시작, 현재 페이지: $page")

        val apiCall = { RetrofitClient.battleApiService.getBattleComments(battleId, page) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    Log.d("BATTLE_COMMENT_LOAD", "불러온 댓글 개수: ${response.result.battleCommentPreviewList.size}")

                    comments.addAll(response.result.battleCommentPreviewList)
                    commentAdapter.notifyDataSetChanged()
                    hasNext = response.result.hasNext
                    page++

                    updateNoCommentTextViewVisibility()
                } else {
                    Log.e("BATTLE_COMMENT_LOAD", "댓글 불러오기 실패: ${response.message}")
                }
                isLoading = false
            },
            onFailure = { t ->
                Log.e("BATTLE_COMMENT_LOAD", "댓글 불러오기 에러: ${t.message}")
                isLoading = false
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }

    private fun postComment() {
        val content = binding.commentEditText.text.toString().trim()
        if (content.isEmpty()) return

        val apiCall = { RetrofitClient.battleApiService.postBattleComment(battleId, CommentRequest(content)) }
        if (battleId <= 0) {
            Toast.makeText(context, "유효하지 않은 배틀 ID입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("POST_COMMENT", "Posting comment: $content to battleId: $battleId")



        binding.commentEditText.text.clear()

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                Log.d("POST_COMMENT", "Comment post success: ${response.isSuccess}")
                if (response.isSuccess) {
                   // val myClositId = TokenUtils.getClositId(requireContext()) ?: ""
                    val newComment = BattleComment(
                        battleCommentId = response.result.battleCommentId,
                        clositId = response.result.clositId,
                        content = content,
                        createdAt = response.result.createdAt
                    )
                    // 댓글이 없었을 때 첫 댓글인 경우
                    if (comments.isEmpty()) {
                        comments.add(newComment)
                        commentAdapter.notifyItemInserted(0)
                        binding.commentsRecyclerView.scrollToPosition(0)
                        commentAdapter.notifyDataSetChanged() // <- 이거 필수
                        Log.d("COMMENT_LOAD", "불러온 댓글 개수: ${comments.size}")

                        // 강제로 뷰 상태 업데이트 (이게 중요함)
                        updateNoCommentTextViewVisibility()
                    } else {
                        // 이미 댓글이 있었던 경우
                        comments.add(0, newComment)
                        commentAdapter.notifyItemInserted(0)
                        binding.commentsRecyclerView.scrollToPosition(0)
                    }
                    /*
                    comments.add(0, newComment)
                    commentAdapter.notifyItemInserted(0)
                    binding.commentsRecyclerView.scrollToPosition(0)
                    updateNoCommentTextViewVisibility()

                     */
                } else {
                    Log.e("POST_COMMENT", "API responded with failure: ${response.message}")
                    Toast.makeText(context, "댓글 작성 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }

            },
            onFailure = { t ->
                Toast.makeText(context, "댓글 작성 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("POST_COMMENT", "API 호출 실패: ${t.message}")
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }

    private fun deleteComment(commentId: Int) {
        val apiCall = { RetrofitClient.battleApiService.deleteBattleComment(battleId, commentId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    val position = comments.indexOfFirst { it.battleCommentId == commentId }
                    if (position != -1) {
                        comments.removeAt(position)
                        commentAdapter.notifyItemRemoved(position)
                        updateNoCommentTextViewVisibility()
                    }
                }
            },
            onFailure = { t ->
                Toast.makeText(context, "댓글 삭제 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }

    private fun updateNoCommentTextViewVisibility() {
        if (comments.isEmpty()) {
            binding.tvNoComment.visibility = View.VISIBLE
            binding.commentsRecyclerView.visibility = View.GONE
        } else {
            binding.tvNoComment.visibility = View.GONE
            binding.commentsRecyclerView.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance(battleId: Long): BattleCommentBottomSheetFragment {
            val fragment = BattleCommentBottomSheetFragment()
            val args = Bundle()
            args.putLong("battleId", battleId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(timeUpdateRunnable, 10000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(timeUpdateRunnable)
    }
}
