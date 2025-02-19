package com.example.umc_closit.ui.timeline.comment

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
import com.example.umc_closit.data.remote.timeline.CommentItem
import com.example.umc_closit.data.remote.timeline.CommentRequest
import com.example.umc_closit.databinding.FragmentCommentBottomSheetBinding
import com.example.umc_closit.utils.TokenUtils

class CommentBottomSheetFragment : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCommentBottomSheetBinding
    private lateinit var commentAdapter: CommentAdapter
    private val comments = mutableListOf<CommentItem>()

    private var postId: Int = -1
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
        postId = arguments?.getInt("postId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentBottomSheetBinding.inflate(inflater, container, false)

        commentAdapter = CommentAdapter(comments, ::deleteComment)
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.commentsRecyclerView.adapter = commentAdapter

        val itemTouchHelper = ItemTouchHelper(CommentSwipeCallback(commentAdapter))
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

        Log.d("COMMENT_LOAD", "댓글 로드 시작, 현재 페이지: $page")

        val apiCall = { RetrofitClient.timelineService.getComments(postId, page) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    Log.d("COMMENT_LOAD", "불러온 댓글 개수: ${response.result.commentPreviewList.size}")

                    comments.addAll(response.result.commentPreviewList)
                    commentAdapter.notifyDataSetChanged()
                    hasNext = response.result.hasNext
                    page++

                    updateNoCommentTextViewVisibility()
                } else {
                    Log.e("COMMENT_LOAD", "댓글 불러오기 실패: ${response}")
                }
                isLoading = false
            },
            onFailure = { t ->
                Log.e("COMMENT_LOAD", "댓글 불러오기 에러: ${t.message}")
                isLoading = false
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }


    private fun postComment() {
        val content = binding.commentEditText.text.toString().trim()
        if (content.isEmpty()) return

        val apiCall = { RetrofitClient.timelineService.postComment(postId, CommentRequest(content)) }

        binding.commentEditText.text.clear()

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    val newComment = CommentItem(
                        commentId = response.result.commentId,
                        clositId = response.result.clositId,
                        content = content,
                        createdAt = response.result.createdAt,
                        name = null,
                        profileImage = null
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
                }
            },
            onFailure = { t ->
                Toast.makeText(context, "댓글 작성 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }


    private fun deleteComment(commentId: Int) {
        val apiCall = { RetrofitClient.timelineService.deleteComment(postId, commentId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    val position = comments.indexOfFirst { it.commentId == commentId }
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

    /**
     * 댓글 없을 때 "아직 댓글이 없습니다" TextView 표시/숨김
     */
    private fun updateNoCommentTextViewVisibility() {
        Log.d("COMMENT_VIEW", "댓글 목록 개수: ${comments.size}")

        if (comments.isEmpty()) {
            binding.tvNoComment.visibility = View.VISIBLE
            binding.commentsRecyclerView.visibility = View.GONE
            Log.d("COMMENT_VIEW", "tvNoComment 보임")
        } else {
            binding.tvNoComment.visibility = View.GONE
            binding.commentsRecyclerView.visibility = View.VISIBLE
            Log.d("COMMENT_VIEW", "댓글 리사이클러뷰 보임")
        }
    }


    companion object {
        fun newInstance(postId: Int): CommentBottomSheetFragment {
            val fragment = CommentBottomSheetFragment()
            val args = Bundle()
            args.putInt("postId", postId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(timeUpdateRunnable, 10000) // 10초마다 실행 시작
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(timeUpdateRunnable)
    }


}
