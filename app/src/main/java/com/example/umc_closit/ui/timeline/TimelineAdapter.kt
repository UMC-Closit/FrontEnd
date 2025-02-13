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
import com.example.umc_closit.data.remote.BookmarkRequest
import com.example.umc_closit.data.remote.BookmarkResponse
import com.example.umc_closit.data.remote.LikeResponse
import com.example.umc_closit.data.remote.PostPreview
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.TimelineService
import com.example.umc_closit.databinding.ItemTimelineBinding
import com.example.umc_closit.ui.timeline.comment.CommentBottomSheetFragment
import com.example.umc_closit.ui.timeline.detail.DetailActivity
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
        val item = timelineItems[position]

        with(holder.binding) {
            // 🔥 API에서 받은 이미지 로드
            Glide.with(context).load(item.frontImage).into(ivImageBig)
            Glide.with(context).load(item.backImage).into(ivImageSmall)
            Glide.with(context).load(item.profileImage).transform(CircleCrop()).into(ivUserProfile)

            // 좋아요/저장 상태 설정
            ivLike.setImageResource(if (item.isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off)
            ivSave.setImageResource(if (item.isSaved) R.drawable.ic_save_on else R.drawable.ic_save_off)

            // 📌 게시글 상세 페이지로 이동
            ivImageBig.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("timelineItem", item)
                context.startActivity(intent)
            }

            // 📌 댓글 버튼 클릭 이벤트
            ivComment.setOnClickListener {
                val commentFragment = CommentBottomSheetFragment.newInstance()
                commentFragment.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager, commentFragment.tag)
            }

/*            // 📌 좋아요 버튼 클릭 이벤트(토큰 있는 버전)
            ivLike.setOnClickListener {
                timelineService.likePost("Bearer $accessToken", item.postId, userId)
                    .enqueue(object : Callback<LikeResponse> {
                        override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                            if (response.isSuccessful) {
                                response.body()?.let { result ->
                                    if (result.isSuccess) {
                                        // 서버 응답을 바탕으로 좋아요 상태 업데이트
                                        val isLiked = result.result.isLiked
                                        timelineItems[position] = item.copy(isLiked = isLiked)
                                        notifyItemChanged(position)

                                        Toast.makeText(context, if (isLiked) "좋아요!" else "좋아요 취소!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }*/

            // 📌 좋아요 버튼 클릭 이벤트(토큰 없는 버전)
            ivLike.setOnClickListener {
                timelineService.likePost(item.postId, userId)
                    .enqueue(object : Callback<LikeResponse> {
                        override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                            if (response.isSuccessful) {
                                response.body()?.let { result ->
                                    if (result.isSuccess) {
                                        val isLiked = result.result.isLiked
                                        timelineItems[position] = item.copy(isLiked = isLiked)
                                        notifyItemChanged(position)

                                        Toast.makeText(context, if (isLiked) "좋아요!" else "좋아요 취소!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }


            // 📌 저장 버튼 클릭 이벤트
            ivSave.setOnClickListener {
                val newSaveState = !item.isSaved

                if (newSaveState) {
                    timelineService.savePost(BookmarkRequest(userId, item.postId))
                        .enqueue(object : Callback<BookmarkResponse> {
                            override fun onResponse(call: Call<BookmarkResponse>, response: Response<BookmarkResponse>) {
                                if (response.isSuccessful && response.body()?.isSuccess == true) {
                                    timelineItems[position] = item.copy(isSaved = true)
                                    notifyItemChanged(position)
                                    Toast.makeText(context, "저장됨!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<BookmarkResponse>, t: Throwable) {
                                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {
                    // 저장 취소 로직은 아직 완성 안되었으므로 토스트만 처리
                    timelineItems[position] = item.copy(isSaved = false)
                    notifyItemChanged(position)
                    Toast.makeText(context, "저장 취소", Toast.LENGTH_SHORT).show()
                }
            }


            // 📌 유저 프로필 클릭 이벤트
            ivUserProfile.setOnClickListener {
                Toast.makeText(context, "유저 프로필 클릭됨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateTimelineItems(updatedItems: List<PostPreview>) {
        this.timelineItems = updatedItems.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = timelineItems.size
}
