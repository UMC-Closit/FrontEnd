package com.example.umc_closit.ui.battle.comment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.battle.BattleComment
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.ProfileUserResponse
import com.example.umc_closit.databinding.ItemCommentBinding
import com.example.umc_closit.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BattleCommentAdapter(
    private val commentList: MutableList<BattleComment>,
    private val onDeleteComment: (Int) -> Unit
) : RecyclerView.Adapter<BattleCommentAdapter.BattleCommentViewHolder>() {

    inner class BattleCommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: BattleComment) {
            with(binding) {
                tvUserName.text = comment.clositId
                tvCommentText.text = comment.content
                tvCreateTime.text = DateUtils.getTimeAgo(comment.createdAt)

                Glide.with(root.context)
                    .load(R.drawable.img_profile_default)
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
