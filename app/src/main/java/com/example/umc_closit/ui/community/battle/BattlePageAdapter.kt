package com.example.umc_closit.Community

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R
import com.example.umc_closit.data.entities.BattleItem
import com.example.umc_closit.databinding.ItemBattleMainBinding
import com.example.umc_closit.data.BattleViewModel
import com.example.umc_closit.data.VoteResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.ui.timeline.comment.CommentBottomSheetFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BattlePageAdapter(
    private val context: Context,
    private var battleItems: MutableList<BattleItem>
) : RecyclerView.Adapter<BattlePageAdapter.ViewHolder>() {

    // ViewModelProvider 수정: AndroidX Lifecycle 방식
    private val battleViewModel by lazy {
        ViewModelProvider(
            context as AppCompatActivity,
            ViewModelProvider.AndroidViewModelFactory(context.application)
        )[BattleViewModel::class.java]
    }

    private val apiService = RetrofitClient.battleApiService

    class ViewHolder(val binding: ItemBattleMainBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBattleMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = battleItems[position]

        with(holder.binding) {
            // 좌측 battleID 표시
            tvLeftVote.text = "Left: ${item.battleId}"

            // 우측 battleID 표시
            tvRightVote.text = "Right: ${item.battleId}"

            // 댓글 클릭 시 CommentBottomSheetFragment 호출
            ivComment.setOnClickListener {
                CommentBottomSheetFragment.newInstance().show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "comment"
                )
            }

            // 좋아요 상태 반영
            val isLiked = battleViewModel.getLikeStatus(item.id) ?: false
            ivLike.setImageResource(if (isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off)

            // 좋아요 버튼 클릭 이벤트
            ivLike.setOnClickListener {
                val newLikeState = !isLiked
                battleViewModel.updateLikeStatus(item.id, newLikeState)
                ivLike.setImageResource(
                    if (newLikeState) R.drawable.ic_like_on else R.drawable.ic_like_off
                )

//                if (newLikeState) {
//                    // 좋아요 추가 API
//                    apiService.addBattleLike(item.battleId).enqueue(createLikeCallback("좋아요!"))
//                } else {
//                    // 좋아요 취소 API (battleLikeId 반드시 확인)
//                    apiService.removeBattleLike(item.battleId, item.battleLikeId)
//                        .enqueue(createLikeCallback("좋아요 취소!"))
//                }
                notifyItemChanged(position) // RecyclerView 갱신
            }


            // 투표 버튼 클릭 이벤트
            tvLeftVote.setOnClickListener { sendVote(item.battleId, item.leftPostId, voteProgressBar) }
            tvRightVote.setOnClickListener { sendVote(item.battleId, item.rightPostId, voteProgressBar) }
        }
    }

    /**
     * 투표 요청 처리
     */
    private fun sendVote(battleId: Long, postId: Long, progressBar: ProgressBar) {
        apiService.voteBattle(battleId, mapOf("postId" to postId)).enqueue(object : Callback<VoteResponse> {
            override fun onResponse(call: Call<VoteResponse>, response: Response<VoteResponse>) {
                response.body()?.let {
                    if (it.isSuccess) {
                        val total = it.result.firstVotingRate + it.result.secondVotingRate
                        val progress = if (total > 0) it.result.firstVotingRate * 100 / total else 50
                        animateProgress(progressBar, progress)

                        // 투표 결과 Toast
                        Toast.makeText(
                            context,
                            "투표: ${it.result.firstVotingRate}% vs ${it.result.secondVotingRate}%",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<VoteResponse>, t: Throwable) {
                Log.e("Vote", "투표 실패: ${t.localizedMessage}")
            }
        })
    }

    /**
     * ProgressBar 애니메이션
     */
    private fun animateProgress(progressBar: ProgressBar, target: Int) {
        ValueAnimator.ofInt(progressBar.progress, target).apply {
            duration = 800L
            addUpdateListener { progressBar.progress = it.animatedValue as Int }
            start()
        }
    }

    /**
     * 좋아요 요청 처리
     */
    private fun createLikeCallback(message: String) = object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.e("Like", "$message 실패: ${t.localizedMessage}")
        }
    }

    override fun getItemCount(): Int = battleItems.size
}
