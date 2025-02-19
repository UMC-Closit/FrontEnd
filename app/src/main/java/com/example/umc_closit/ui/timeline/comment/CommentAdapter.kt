package com.example.umc_closit.ui.timeline.comment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.ProfileUserResponse
import com.example.umc_closit.data.remote.timeline.CommentItem
import com.example.umc_closit.databinding.ItemCommentBinding
import com.example.umc_closit.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentAdapter(
    private val commentList: MutableList<CommentItem>,
    private val onDeleteComment: (Int) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentItem) {
            with(binding) {
                tvUserName.text = comment.name ?: comment.clositId
                tvCommentText.text = comment.content ?: ""  // 여기가 중요!
                tvCreateTime.text = DateUtils.getTimeAgo(comment.createdAt)

                // 사용자 정보 없으면 API 요청
                if (comment.name == null || comment.profileImage == null) {
                    Log.d("COMMENT","사용자 정보 없으므로 요청: ${comment.clositId}")
                    fetchUserInfo(comment.clositId, position)
                }

                Glide.with(root.context)
                    .load(comment.profileImage ?: R.drawable.img_profile_default)
                    .circleCrop()
                    .into(ivUserProfile)
            }
        }

        fun updateTime(comment: CommentItem) {
            binding.tvCreateTime.text = DateUtils.getTimeAgo(comment.createdAt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        Log.d("COMMENT", "bind - position: $position, content: ${comment.content}")
        holder.bind(comment)

        Log.d("COMMENT","사용자 정보 없으므로 요청 전: ${comment.name}, ${comment.profileImage}")

        // 사용자 정보 없으면 API 요청
        if (comment.name == null || comment.profileImage == null) {
            Log.d("COMMENT","사용자 정보 없으므로 요청: ${comment.clositId}")
            fetchUserInfo(comment.clositId, position)
        }
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int, payloads: MutableList<Any>) {
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
        onDeleteComment(comment.commentId)
        commentList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun isUserComment(position: Int, clositId: String): Boolean {
        return commentList[position].clositId == clositId
    }

    // 사용자 정보 가져오기
    private fun fetchUserInfo(clositId: String, position: Int) {
        RetrofitClient.profileService.getUserProfile(clositId)
            .enqueue(object : Callback<ProfileUserResponse> {
                override fun onResponse(call: Call<ProfileUserResponse>, response: Response<ProfileUserResponse>) {
                    try {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            Log.d("COMMENT", "responseBody: $responseBody")

                            if (responseBody?.isSuccess == true) {
                                val userInfo = responseBody.result
                                commentList[position].name = userInfo?.name
                                commentList[position].profileImage = userInfo?.profileImage

                                Log.d("COMMENT", "position: $position, name: ${userInfo?.name}, profileImage: ${userInfo?.profileImage}")
                                notifyItemChanged(position)
                            } else {
                                Log.e("COMMENT", "position: $position, response 실패: ${response.errorBody()?.string()}")
                            }
                        } else {
                            Log.e("COMMENT", "position: $position, response 실패: ${response.errorBody()?.string()}")
                        }
                    } catch (e: Exception) {
                        Log.e("COMMENT", "position: $position, 파싱 중 에러: ${e.message}")
                        e.printStackTrace()
                    }
                }


                override fun onFailure(call: Call<ProfileUserResponse>, t: Throwable) {
                    // 실패 시 무시 (기본 값 유지)
                    Log.e("COMMENT", "position: $position, error: ${t.message}")

                }
            })
    }
}
