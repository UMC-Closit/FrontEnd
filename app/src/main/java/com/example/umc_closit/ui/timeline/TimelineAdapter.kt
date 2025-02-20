package com.example.umc_closit.ui.timeline

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.timeline.BookmarkCreateResponse
import com.example.umc_closit.data.remote.timeline.BookmarkDeleteResponse
import com.example.umc_closit.data.remote.timeline.BookmarkRequest
import com.example.umc_closit.data.remote.timeline.LikeResponse
import com.example.umc_closit.data.remote.timeline.PostPreview
import com.example.umc_closit.databinding.ItemTimelineBinding
import com.example.umc_closit.ui.timeline.comment.CommentBottomSheetFragment
import com.example.umc_closit.ui.timeline.detail.DetailActivity
import com.example.umc_closit.utils.TokenUtils

class TimelineAdapter(
    private val context: Context,
    private var timelineItems: MutableList<PostPreview>,
) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    private val timelineService = RetrofitClient.timelineService

    class TimelineViewHolder(val binding: ItemTimelineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = timelineItems[position] ?: return

        with(holder.binding) {
            Glide.with(context).load(item.frontImage).into(ivImageBig)
            Glide.with(context).load(item.backImage).into(ivImageSmall)
            Glide.with(context).load(item.profileImage).transform(CircleCrop()).into(ivUserProfile)

            ivLike.setImageResource(if (item.isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off)
            ivSave.setImageResource(if (item.isSaved) R.drawable.ic_save_on else R.drawable.ic_save_off)

            ivImageBig.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("timelineItem", item)
                context.startActivity(intent)
            }

            // TimelineAdapter.kt
            ivComment.setOnClickListener {
                val commentFragment = CommentBottomSheetFragment.newInstance(item.postId)
                commentFragment.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager, commentFragment.tag)
            }

            ivLike.setOnClickListener {
                if (item.isLiked) {
                    val apiCall = { timelineService.removeLike(item.postId) }
                    TokenUtils.handleTokenRefresh(
                        call = apiCall(),
                        onSuccess = { result: LikeResponse ->
                            if (result.isSuccess) {
                                timelineItems[position] = item.copy(isLiked = false)
                                notifyItemChanged(position)
                            }
                        },
                        onFailure = { t ->
                            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        },
                        retryCall = apiCall,
                        context = context
                    )
                } else {
                    val apiCall = { timelineService.addLike(item.postId) }
                    TokenUtils.handleTokenRefresh(
                        call = apiCall(),
                        onSuccess = { result: LikeResponse ->
                            if (result.isSuccess) {
                                timelineItems[position] = item.copy(isLiked = true)
                                notifyItemChanged(position)
                            }
                        },
                        onFailure = { t ->
                            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        },
                        retryCall = apiCall,
                        context = context
                    )
                }
            }

            ivSave.setOnClickListener {
                val newSaveState = !item.isSaved

                if (newSaveState) {
                    val apiCall = {
                        timelineService.addBookmark(BookmarkRequest(item.postId))
                    }

                    TokenUtils.handleTokenRefresh(
                        call = apiCall(),
                        onSuccess = { response: BookmarkCreateResponse ->
                            if (response.isSuccess) {
                                timelineItems[position] = item.copy(isSaved = true)
                                notifyItemChanged(position)
                            } else {
                                Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onFailure = { t ->
                            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        },
                        retryCall = apiCall,
                        context = context
                    )

                } else {
                    val apiCall = {
                        timelineService.removeBookmark(item.postId)
                    }

                    TokenUtils.handleTokenRefresh(
                        call = apiCall(),
                        onSuccess = { response: BookmarkDeleteResponse ->
                            if (response.isSuccess) {
                                timelineItems[position] = item.copy(isSaved = false)
                                notifyItemChanged(position)
                            } else {
                                Toast.makeText(context, "저장 취소 실패", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onFailure = { t ->
                            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT)
                                .show()
                        },
                        retryCall = apiCall,
                        context = context
                    )
                }

            }


            ivUserProfile.setOnClickListener {
                Toast.makeText(context, "유저 프로필 클릭됨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateTimelineItems(updatedItems: List<PostPreview>) {
        this.timelineItems.clear()
        this.timelineItems.addAll(updatedItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = timelineItems.size
}
