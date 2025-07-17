package com.example.umc_closit.ui.battle.comment

import BattleComment
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.BaseResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.ProfileUserResponse
import com.example.umc_closit.databinding.ItemCommentBinding
import com.example.umc_closit.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BattleCommentAdapter(
    private val commentList: MutableList<BattleComment>,
    private val onDeleteComment: (Int) -> Unit,
    private val onReplyClick: (BattleComment) -> Unit
) : RecyclerView.Adapter<BattleCommentAdapter.BattleCommentViewHolder>() {

    inner class BattleCommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: BattleComment) {
            with(binding) {

                tvLikeCount.setOnClickListener {
                    onReplyClick(comment)
                }

                val content = comment.content
                val spannable = SpannableStringBuilder(content)
                val mentionRegex = Regex("@(\\w+)")
                val matches = mentionRegex.findAll(content)

                matches.forEach { match ->
                    val id = match.groupValues[1]  // '@' 제외한 아이디

                    RetrofitClient.authService.checkIdUnique(id).enqueue(object : Callback<BaseResponse<Boolean>> {
                        override fun onResponse(
                            call: Call<BaseResponse<Boolean>>,
                            response: Response<BaseResponse<Boolean>>
                        ) {
                            val exists = response.body()?.result == false // 이미 있는 아이디
                            if (exists) {
                                spannable.setSpan(
                                    StyleSpan(Typeface.BOLD),
                                    match.range.first,
                                    match.range.last + 1,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                binding.tvCommentText.text = spannable
                            }
                        }

                        override fun onFailure(call: Call<BaseResponse<Boolean>>, t: Throwable) {
                            // 실패해도 무시
                            Log.e("CommentBind", "ID 확인 실패: $id")
                        }
                    })
                }

// 초기 텍스트는 일단 전체 그대로 넣어두기
                binding.tvCommentText.text = spannable
                tvCreateTime.text = DateUtils.getTimeAgo(comment.createdAt)

                // 대댓글 여부에 따라 여백 - 가로 너비의 10%
                val isReply = comment.parentBattleCommentId != 0L
                val screenWidth = clProfile.resources.displayMetrics.widthPixels
                val paddingStart = if (isReply) (screenWidth * 0.1).toInt() else 0

                clProfile.setPaddingRelative(
                    paddingStart,
                    clProfile.paddingTop,
                    clProfile.paddingEnd,
                    clProfile.paddingBottom
                )

                tvUserName.text = comment.clositId

                Glide.with(root.context)
                    .load(comment.thumbnail)  // 🔥 실제 썸네일 적용
                    .placeholder(R.drawable.img_profile_default) // 로딩 중 기본 이미지
                    .error(R.drawable.img_profile_default)       // 에러 시 기본 이미지
                    .circleCrop()
                    .into(ivUserProfile)
            }
        }

        fun updateTime(comment: BattleComment) {
            binding.tvCreateTime.text = DateUtils.getTimeAgo(comment.createdAt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BattleCommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BattleCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BattleCommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.bind(comment)
        fetchUserInfo(comment.clositId, holder)
    }

    override fun onBindViewHolder(holder: BattleCommentViewHolder, position: Int, payloads: MutableList<Any>) {
        val comment = commentList[position]
        if (payloads.isNotEmpty() && payloads[0] == "timeUpdate") {
            holder.updateTime(comment)
        } else {
            holder.bind(comment)
        }
    }

    override fun getItemCount(): Int = commentList.size

    fun updateTimeForAllItems() {
        for (i in commentList.indices) {
            notifyItemChanged(i, "timeUpdate")
        }
    }

    fun removeItem(position: Int) {
        val comment = commentList[position]
        onDeleteComment(comment.battleCommentId)
        commentList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun isUserComment(position: Int, myClositId: String): Boolean {
        return commentList[position].clositId == myClositId
    }

    private fun fetchUserInfo(clositId: String, holder: BattleCommentViewHolder) {
        RetrofitClient.profileService.getUserProfile(clositId)
            .enqueue(object : Callback<ProfileUserResponse> {
                override fun onResponse(call: Call<ProfileUserResponse>, response: Response<ProfileUserResponse>) {
                    if (response.isSuccessful) {
                        val userInfo = response.body()?.result
                        if (userInfo != null) {
                            holder.binding.tvUserName.text = userInfo.name ?: clositId
                            Glide.with(holder.binding.root.context)
                                .load(userInfo.profileImage ?: R.drawable.img_profile_default)
                                .circleCrop()
                                .into(holder.binding.ivUserProfile)
                        }
                    } else {
                        Log.e("BattleCommentAdapter", "Failed to fetch user info: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ProfileUserResponse>, t: Throwable) {
                    Log.e("BattleCommentAdapter", "Error fetching user info: ${t.message}")
                }
            })
    }
}
