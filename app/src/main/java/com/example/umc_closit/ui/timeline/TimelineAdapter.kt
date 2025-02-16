package com.example.umc_closit.ui.timeline

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.timeline.BookmarkRequest
import com.example.umc_closit.data.remote.timeline.BookmarkResponse
import com.example.umc_closit.data.remote.timeline.LikeResponse
import com.example.umc_closit.data.remote.timeline.PostPreview
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.databinding.ItemTimelineBinding
import com.example.umc_closit.ui.timeline.comment.CommentBottomSheetFragment
import com.example.umc_closit.ui.timeline.detail.DetailActivity
import com.example.umc_closit.utils.TokenUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimelineAdapter(
    private val context: Context,
    private var timelineItems: MutableList<PostPreview>,
    private val accessToken: String, // 로그인 유지된 토큰
    private val userId: Int // 로그인한 사용자 ID
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
            // API에서 받은 이미지 로드
            Glide.with(context).load(item.frontImage).into(ivImageBig)
            Glide.with(context).load(item.backImage).into(ivImageSmall)
            Glide.with(context).load(item.profileImage).transform(CircleCrop()).into(ivUserProfile)

            // 좋아요/저장 상태 설정
            ivLike.setImageResource(if (item.isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off)
            ivSave.setImageResource(if (item.isSaved) R.drawable.ic_save_on else R.drawable.ic_save_off)

            // 게시글 상세 페이지로 이동
            ivImageBig.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("timelineItem", item)
                context.startActivity(intent)
            }

            // 댓글 버튼 클릭 이벤트
            ivComment.setOnClickListener {
                val commentFragment = CommentBottomSheetFragment.newInstance()
                commentFragment.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager, commentFragment.tag)
            }


            // 좋아요 버튼 클릭 이벤트
            ivLike.setOnClickListener {
                val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                val token = "Bearer ${sharedPreferences.getString("accessToken", "") ?: ""}"

                val apiCall = {
                    timelineService.likePost(token, item.postId, userId)
                }

                TokenUtils.handleTokenRefresh(
                    call = apiCall(),
                    onSuccess = { result: LikeResponse ->
                        if (result.isSuccess) {
                            val isLiked = result.result.isLiked
                            timelineItems[position] = item.copy(isLiked = isLiked)
                            notifyItemChanged(position)

                            Toast.makeText(context, if (isLiked) "좋아요!" else "좋아요 취소!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFailure = { t ->
                        Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("LIKE_ERROR", "네트워크 오류: ${t.message}")
                    },
                    retryCall = apiCall,
                    context = context
                )
            }

            // 저장 버튼 클릭 이벤트
            ivSave.setOnClickListener {
                val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                val token = "Bearer ${sharedPreferences.getString("accessToken", "") ?: ""}"

                val newSaveState = !item.isSaved

                if (newSaveState) {
                    val apiCall = {
                        timelineService.savePost(token, BookmarkRequest(userId, item.postId))
                    }

                    TokenUtils.handleTokenRefresh(
                        call = apiCall(),
                        onSuccess = { response: BookmarkResponse ->
                            if (response.isSuccess) {
                                timelineItems[position] = item.copy(isSaved = true)
                                notifyItemChanged(position)
                                Toast.makeText(context, "저장됨!", Toast.LENGTH_SHORT).show()
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
                    timelineItems[position] = item.copy(isSaved = false)
                    notifyItemChanged(position)
                    Toast.makeText(context, "저장 취소", Toast.LENGTH_SHORT).show()
                }
            }



            // 유저 프로필 클릭 이벤트
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
