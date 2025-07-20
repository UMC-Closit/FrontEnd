package com.example.umc_closit.ui.battle.comment

import BattleComment
import CommentRequest
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

    private var replyingToCommentId: Long? = null

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

        commentAdapter = BattleCommentAdapter(comments, ::deleteComment, ::onReplyClick)
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

    private fun onReplyClick(comment: BattleComment) {
        replyingToCommentId = comment.battleCommentId.toLong()

        // "@닉네임 " 자동입력
        binding.commentEditText.setText("@${comment.clositId} ")
        binding.commentEditText.setSelection(binding.commentEditText.text.length)

        // 키보드 포커스
        binding.commentEditText.requestFocus()
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(binding.commentEditText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }

    private fun loadComments() {
        if (!hasNext || isLoading) {
            Log.d("BATTLE_COMMENT_LOAD", "로딩 중단 - hasNext: $hasNext, isLoading: $isLoading")
            return
        }
        isLoading = true
        Log.d("BATTLE_COMMENT_LOAD", "댓글 로딩 시작 - page: $page")

        val apiCall = { RetrofitClient.battleApiService.getBattleComments(battleId, page) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                Log.d("BATTLE_COMMENT_LOAD", "응답 성공 - isSuccess: ${response.isSuccess}, message: ${response.message}")

                val result = response.result
                if (response.isSuccess && result != null) {
                    val newComments = result.battleCommentPreviewList ?: emptyList()
                    val sorted = sortCommentsWithReplies(newComments)

                    comments.clear()
                    comments.addAll(sorted)
                    commentAdapter.notifyDataSetChanged()

                    hasNext = result.hasNext
                    page++

                    updateNoCommentTextViewVisibility()
                } else {
                    Log.e("BATTLE_COMMENT_LOAD", "댓글 불러오기 실패 - message: ${response.message}, result: $result")
                }
                isLoading = false
            },
            onFailure = { t ->
                Log.e("BATTLE_COMMENT_LOAD", "댓글 불러오기 네트워크 오류 - ${t.message}")
                isLoading = false
            },
            context = requireContext()
        )
    }

    private fun postComment() {
        val content = binding.commentEditText.text.toString().trim()
        if (content.isEmpty()) {
            Log.d("BATTLE_COMMENT", "입력된 내용이 비어 있음, 요청 중단")
            return
        }

        if (battleId <= 0) {
            Log.e("BATTLE_COMMENT", "유효하지 않은 battleId: $battleId")
            Toast.makeText(context, "유효하지 않은 배틀 ID입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val parentId = replyingToCommentId
        Log.d("BATTLE_COMMENT", "댓글 작성 요청 준비 - content: \"$content\", battleId: $battleId, parentId: ${parentId ?: "없음"}")

        val apiCall = {
            RetrofitClient.battleApiService.postBattleComment(
                battleId,
                CommentRequest(content, parentId)
            )
        }

        binding.commentEditText.text.clear()
        replyingToCommentId = null

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                Log.d("BATTLE_COMMENT", "응답 수신 - isSuccess: ${response.isSuccess}, message: ${response.message}")

                if (response.isSuccess) {
                    val result = response.result
                    Log.d("BATTLE_COMMENT", "작성된 댓글 ID: ${result.battleCommentId}, 작성자: ${result.clositId}, 작성시각: ${result.createdAt}")

                    val newComment = BattleComment(
                        battleCommentId = result.battleCommentId,
                        clositId = result.clositId,
                        content = content,
                        parentBattleCommentId = parentId ?: 0,
                        thumbnail = "string", // 실제 썸네일로 대체 필요
                        createdAt = result.createdAt
                    )

                    val insertIndex = if (parentId != null && parentId != 0L) {
                        comments.indexOfLast { it.battleCommentId.toLong() == parentId } + 1
                    } else {
                        comments.indexOfLast { it.parentBattleCommentId == 0L } + 1
                    }
                    comments.add(newComment)
                    val sorted = sortCommentsWithReplies(comments)
                    comments.clear()
                    comments.addAll(sorted)
                    commentAdapter.notifyDataSetChanged()

                    updateNoCommentTextViewVisibility()
                } else {
                    Log.e("BATTLE_COMMENT", "API 실패 - ${response.message}")
                    Toast.makeText(context, "댓글 작성 실패: ${response}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Log.e("BATTLE_COMMENT", "네트워크 오류 - ${t}")
                Toast.makeText(context, "댓글 작성 실패: ${t}", Toast.LENGTH_SHORT).show()
            },
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

    private fun sortCommentsWithReplies(comments: List<BattleComment>): List<BattleComment> {
        val result = mutableListOf<BattleComment>()
        val commentMap = comments.groupBy { it.parentBattleCommentId }

        fun addReplies(parentId: Long) {
            commentMap[parentId]?.forEach { comment ->
                result.add(comment)
                addReplies(comment.battleCommentId.toLong())
            }
        }

        addReplies(0L)
        return result
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
