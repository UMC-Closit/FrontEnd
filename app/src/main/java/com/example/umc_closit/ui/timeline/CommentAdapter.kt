package com.example.umc_closit.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.data.Comment
import com.example.umc_closit.databinding.ItemCommentBinding
import com.example.umc_closit.utils.DateUtils
import com.bumptech.glide.Glide
import com.example.umc_closit.R

class CommentAdapter(private var commentList: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        with(holder.binding) {
            tvUserName.text = comment.userInfo?.userName ?: "Unknown"
            tvCommentText.text = comment.commentText
            tvCreateTime.text = DateUtils.getTimeAgo(comment.createTime)

            val profileImage = comment.userInfo?.userProfileImage ?: R.drawable.img_profile_default
            Glide.with(holder.itemView.context).load(profileImage).into(ivUserProfile)

            ivLike.setImageResource(
                if (comment.userLike) R.drawable.ic_like_on else R.drawable.ic_like_off
            )

            ivLike.setOnClickListener {
                comment.userLike = !comment.userLike
                comment.likeCount = if (comment.userLike) comment.likeCount + 1 else comment.likeCount - 1

                ivLike.setImageResource(
                    if (comment.userLike) R.drawable.ic_like_on else R.drawable.ic_like_off
                )

                tvLikeCount.text = "좋아요 ${comment.likeCount}개"
                notifyItemChanged(position)
            }

            tvLikeCount.text = "좋아요 ${comment.likeCount}개"
        }
    }

    override fun getItemCount(): Int = commentList.size

}