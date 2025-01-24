package com.example.umc_closit.ui.timeline

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.umc_closit.R
import com.example.umc_closit.data.TimelineItem
import com.example.umc_closit.databinding.ItemTimelineBinding
import com.example.umc_closit.model.TimelineViewModel

class TimelineAdapter(
    private val context: Context,
    private var timelineItems: MutableList<TimelineItem>,
    private val savedPosts: MutableList<TimelineItem>
) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    private val timelineViewModel: TimelineViewModel = ViewModelProvider(context as AppCompatActivity).get(TimelineViewModel::class.java)

    // ViewHolder 정의
    class TimelineViewHolder(val binding: ItemTimelineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val item = timelineItems[position]
        with(holder.binding) {
            ivImageBig.setImageResource(item.mainImageResId)
            ivImageSmall.setImageResource(item.overlayImageResId)

            // 유저 프로필 사진을 동그라미로
            Glide.with(context)
                .load(item.userProfileResId)  // 프로필 이미지
                .transform(CircleCrop())  // 원형으로 자르기
                .into(ivUserProfile)

            // 게시글 상세 페이지로 이동
            ivImageBig.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("timelineItem", item) // 게시글 데이터를 전달
                context.startActivity(intent)
            }

            // 댓글 버튼 클릭 이벤트
            ivComment.setOnClickListener {
                val commentFragment = CommentBottomSheetFragment.newInstance()
                commentFragment.show((context as AppCompatActivity).supportFragmentManager, commentFragment.tag)
            }

            // 좋아요 버튼 클릭 이벤트
            ivLike.setOnClickListener {
                // 좋아요 상태 변경
                timelineViewModel.toggleLike(item.id)

                // 좋아요 상태에 맞게 아이콘 변경
                ivLike.setImageResource(
                    if (item.isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off
                )

                // 좋아요 개수 갱신
                Toast.makeText(context, if (item.isLiked) "좋아요!" else "좋아요 취소!", Toast.LENGTH_SHORT).show()
            }

            // 저장 버튼 클릭 이벤트
            ivSave.setOnClickListener {
                // 저장 상태 변경
                timelineViewModel.toggleSave(item.id)

                // 저장 상태에 맞게 아이콘 변경
                ivSave.setImageResource(
                    if (item.isSaved) R.drawable.ic_save_on else R.drawable.ic_save_off
                )

                // 저장 상태 메시지 표시
                Toast.makeText(context, if (item.isSaved) "저장됨!" else "저장 취소", Toast.LENGTH_SHORT).show()
            }

            // 유저 프로필 클릭 이벤트
            ivUserProfile.setOnClickListener {
                Toast.makeText(context, "유저 프로필 클릭됨", Toast.LENGTH_SHORT).show()
                // 유저 프로필 창 구현은 사용자가 설정
            }
        }
    }

    // updateTimelineItems 메서드 추가: LiveData 업데이트 시 호출
    fun updateTimelineItems(updatedItems: List<TimelineItem>) {
        this.timelineItems = updatedItems.toMutableList()
        notifyDataSetChanged()  // RecyclerView 갱신
    }

    override fun getItemCount(): Int = timelineItems.size
}