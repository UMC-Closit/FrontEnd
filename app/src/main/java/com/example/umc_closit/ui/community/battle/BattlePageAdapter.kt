package com.example.umc_closit.Community

import android.content.Context
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.model.BattleViewModel
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.battle.BattlePreview
import com.example.umc_closit.data.remote.battle.LikeResponse
import com.example.umc_closit.data.remote.battle.VoteResponse
import com.example.umc_closit.databinding.ItemBattleMainBinding
import com.example.umc_closit.ui.battle.comment.BattleCommentBottomSheetFragment
import com.example.umc_closit.utils.FileUtils
import com.example.umc_closit.utils.TokenUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BattlePageAdapter(
    private val context: Context,
    private var battleItems: MutableList<BattlePreview>
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
        val binding = holder.binding

        with(binding) {

            tvTitle.text = item.title

            Glide.with(context).load(item.firstPostBackImage).into(ivLeftSmall)
            Glide.with(context).load(item.firstPostFrontImage).into(ivLeftBig)
            Glide.with(context).load(item.secondPostBackImage).into(ivRightSmall)
            Glide.with(context).load(item.secondPostFrontImage).into(ivRightBig)

            tvLeftId.text = item.firstClositId
            tvRightId.text = item.secondClositId

            tvLikeCount.text = item.likeCount.toString()

            val into = Glide.with(context)
                .load(item.firstProfileImage)
                .placeholder(R.drawable.img_profile_user)
                .error(R.drawable.img_profile_user)
                .centerCrop()
                .circleCrop()
                .into(ivLeftProfile)

            Glide.with(context)
                .load(item.secondProfileImage)
                .placeholder(R.drawable.img_profile_user)
                .error(R.drawable.img_profile_user)
                .centerCrop()
                .circleCrop()
                .into(ivRightProfile)

            tvLeftId.text = "Left: ${item.battleId}"
            tvRightId.text = "Right: ${item.battleId}"

            ivComment.setOnClickListener {
                BattleCommentBottomSheetFragment.newInstance(item.battleId).show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "comment"
                )
            }

            var isFrontImageLeft = true
            var isFrontImageRight = true

            // 왼쪽 이미지 스왑
            binding.ivLeftSmall.setOnClickListener {
                FileUtils.swapImagesWithTagEffect(
                    bigImageView = binding.ivLeftBig,
                    smallImageView = binding.ivLeftSmall,
                    tagContainer = binding.clLeftTagContainer
                ) {
                    isFrontImageLeft = !isFrontImageLeft
                }
            }

            // 오른쪽 이미지 스왑
            binding.ivRightSmall.setOnClickListener {
                FileUtils.swapImagesWithTagEffect(
                    bigImageView = binding.ivRightBig,
                    smallImageView = binding.ivRightSmall,
                    tagContainer = binding.clRightTagContainer
                ) {
                    isFrontImageRight = !isFrontImageRight
                }
            }

            val isLiked = item.liked ?: false
            ivLike.setImageResource(if (isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off_clg)

            ivLike.setOnClickListener {
                val newLikeState = !isLiked
                battleViewModel.updateLikeStatus(item.battleId.toInt(), newLikeState)
                ivLike.setImageResource(if (newLikeState) R.drawable.ic_like_on else R.drawable.ic_like_off_clg)

                if (newLikeState) {
                    apiService.addBattleLike(item.battleId).enqueue(createLikeCallback("좋아요!"))
                } else {
                    apiService.removeBattleLike(item.battleId).enqueue(createLikeCallback("좋아요 취소!"))
                }
            }

            // 투표 클릭 이벤트 처리
            leftArea.setOnClickListener {
                sendVote(item.battleId, item.firstPostId, binding)
            }

            rightArea.setOnClickListener {
                sendVote(item.battleId, item.secondPostId, binding)
            }
        }
    }

    /**
     * 투표 요청 처리 (TokenUtils 적용)
     */
    private fun sendVote(battleId: Long, postId: Int, binding: ItemBattleMainBinding) {
        val requestBody = mapOf("postId" to postId)

        TokenUtils.handleTokenRefresh(
            call = apiService.voteBattle(battleId, requestBody),
            onSuccess = { voteResponse: VoteResponse ->
                if (voteResponse.isSuccess) {
                    val firstVotingRate = voteResponse.result?.firstVotingRate?.toFloat() ?: 0f
                    val secondVotingRate = voteResponse.result?.secondVotingRate?.toFloat() ?: 0f
                    val total = firstVotingRate + secondVotingRate

                    if (total > 0f) {
                        val leftRatio = firstVotingRate / total
                        animateVoteRatio(binding, leftRatio)
                    }

                    Toast.makeText(context, "투표 성공! ${firstVotingRate}% vs ${secondVotingRate}%", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "투표 실패: ${voteResponse.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { throwable ->
                Log.e("Vote", "API 호출 실패", throwable)
                Toast.makeText(context, "네트워크 오류: ${throwable.message}", Toast.LENGTH_SHORT).show()
            },
            context = context
        )
    }

    private fun animateVoteRatio(binding: ItemBattleMainBinding, leftRatio: Float) {
        val rightRatio = 1f - leftRatio
        val constraintLayout = binding.clVote

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintSet.constrainPercentWidth(R.id.left_area, leftRatio)
        constraintSet.constrainPercentWidth(R.id.right_area, rightRatio)

        TransitionManager.beginDelayedTransition(constraintLayout)
        constraintSet.applyTo(constraintLayout)
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