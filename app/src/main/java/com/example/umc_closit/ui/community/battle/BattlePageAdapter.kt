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
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.battle.LikeResponse
import com.example.umc_closit.data.remote.battle.VoteResponse
import com.example.umc_closit.ui.timeline.comment.CommentBottomSheetFragment
import com.example.umc_closit.ui.battle.comment.BattleCommentBottomSheetFragment
import com.example.umc_closit.utils.TokenUtils
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
                BattleCommentBottomSheetFragment.newInstance(item.battleId).show(
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
                ivLike.setImageResource(if (newLikeState) R.drawable.ic_like_on else R.drawable.ic_like_off)

                if (newLikeState) {
                    apiService.addBattleLike(item.battleId).enqueue(createLikeCallback("좋아요!"))
                } else {
                    apiService.removeBattleLike(item.battleLikeId)
                        .enqueue(createLikeCallback("좋아요 취소!"))

                }
            }



            // 투표 버튼 클릭 이벤트
            tvLeftVote.setOnClickListener { sendVote(item.battleId, item.leftPostId, voteProgressBar) }
            tvRightVote.setOnClickListener { sendVote(item.battleId, item.rightPostId, voteProgressBar) }
        }
    }

    /**
     * 투표 요청 처리 (TokenUtils 적용)
     */
    private fun sendVote(battleId: Long, postId: Int, progressBar: ProgressBar) {
        val requestBody = mapOf("postId" to postId)

        TokenUtils.handleTokenRefresh(
            call = apiService.voteBattle(battleId, requestBody),  // 변경된 부분: battleId를 PathVariable로 전달
            onSuccess = { voteResponse: VoteResponse ->
                if (voteResponse.isSuccess) {
                    val firstVotingRate = voteResponse.result?.firstVotingRate ?: 0
                    val secondVotingRate = voteResponse.result?.secondVotingRate ?: 0
                    val totalVotes = firstVotingRate + secondVotingRate

                    val progress = if (totalVotes > 0) {
                        (firstVotingRate * 100) / totalVotes
                    } else {
                        50  // 기본값
                    }

                    animateProgress(progressBar, progress)

                    Toast.makeText(
                        context,
                        "투표 성공! ${firstVotingRate}% vs ${secondVotingRate}%",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "투표 실패: ${voteResponse.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onFailure = { throwable ->
                Log.e("Vote", "API 호출 실패", throwable)
                Toast.makeText(context, "네트워크 오류: ${throwable.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = {
                apiService.voteBattle(battleId, requestBody)  // 재시도 시에도 battleId를 포함
            },
            context = context
        )
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
    private fun createLikeCallback(message: String): Callback<LikeResponse> {
        return object : Callback<LikeResponse> {
            override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess) {
                        Toast.makeText(context, "$message 성공: ${body.result}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "$message 실패: ${body?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                Toast.makeText(context, "$message 실패: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun getItemCount(): Int = battleItems.size
}