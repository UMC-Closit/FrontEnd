package com.example.umc_closit.ui.timeline

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.data.Comment
import com.example.umc_closit.databinding.FragmentCommentBottomSheetBinding
import com.example.umc_closit.utils.DateUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommentBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCommentBottomSheetBinding
    private lateinit var commentAdapter: CommentAdapter
    private val comments = mutableListOf<Comment>() // 댓글 데이터를 직접 관리
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 8_000 // 10초마다 업데이트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBottomSheetBinding.inflate(inflater, container, false)

        // 초기 댓글 데이터 설정
        comments.addAll(
            listOf(
                Comment(1, null, "This is a comment", 0, null, false, "2025-01-17T06:52:07.831513", "2025-01-17T11:11:08.798005", "User1", "This is a comment"),
                Comment(2, null, "Another comment", 0, null, false, "2025-01-16T12:52:07.831513", "2025-01-16T13:11:08.798005", "User2", "Another comment")
            )
        )

        // RecyclerView 초기화
        commentAdapter = CommentAdapter(comments)
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.commentsRecyclerView.adapter = commentAdapter

        // 댓글 입력란 높이 동적 조정
        binding.commentInputLayout.viewTreeObserver.addOnPreDrawListener {
            // commentInputLayout의 높이를 가져와서 RecyclerView의 paddingBottom을 설정
            val inputLayoutHeight = binding.commentInputLayout.height
            binding.commentsRecyclerView.setPadding(
                0, 0, 0, inputLayoutHeight + 10 // commentInputLayout의 높이만큼 paddingBottom 설정
            )
            true
        }

        // 댓글 입력 처리
        binding.submitCommentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                val currentTime = System.currentTimeMillis()
                val formattedTime = DateUtils.getFormattedTime(currentTime)

                val newComment = Comment(
                    commentId = comments.size + 1,
                    userInfo = null,
                    commentText = commentText,
                    likeCount = 0,
                    parentCommentId = null,
                    userLike = false,
                    createTime = formattedTime,
                    updateTime = formattedTime,
                    userName = "New User",
                    body = commentText
                )

                // 댓글 리스트에 추가
                comments.add(newComment)
                commentAdapter.notifyItemInserted(comments.size - 1)

                // 입력창 초기화
                binding.commentEditText.text.clear()
            }
        }

        // 주기적으로 댓글 업데이트
        handler.postDelayed(updateRunnable, updateInterval)

        return binding.root
    }

    // 댓글 시간 주기적 업데이트
    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTime = DateUtils.getFormattedTime(System.currentTimeMillis())
            comments.forEach { comment ->
                comment.updateTime = currentTime
            }
            commentAdapter.notifyDataSetChanged() // 변경 사항 UI에 반영

            // 10초 후 다시 실행
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRunnable) // Fragment가 파괴될 때 핸들러 정리
    }

    companion object {
        fun newInstance(): CommentBottomSheetFragment {
            return CommentBottomSheetFragment()
        }
    }
}