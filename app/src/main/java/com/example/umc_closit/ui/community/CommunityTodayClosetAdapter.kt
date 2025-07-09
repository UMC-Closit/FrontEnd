package com.example.umc_closit.ui.community.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
//import androidx.compose.ui.semantics.contentDescription
//import androidx.compose.ui.semantics.error
//import androidx.compose.ui.semantics.text
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
// data.remote.battle 패키지의 TodayClosetItem을 임포트합니다.
import com.example.umc_closit.data.remote.battle.TodayClosetItem

class CommunityTodayClosetAdapter(
    // 어댑터가 받을 데이터는 실제 아이템 리스트인 List<TodayClosetItem> 입니다.
    private var items: List<TodayClosetItem>,
    private val onItemClick: (TodayClosetItem) -> Unit
) : RecyclerView.Adapter<CommunityTodayClosetAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView as CardView
        val postImageView: ImageView = itemView.findViewById(R.id.img_post)
        val userProfileImageView: ImageView = itemView.findViewById(R.id.img_user_profile)
        val userNameTextView: TextView = itemView.findViewById(R.id.tv_user_id) // 사용자 이름 표시용
        val heartIconImageView: ImageView = itemView.findViewById(R.id.img_heart) // 좋아요 아이콘
        val likesTextView: TextView = itemView.findViewById(R.id.tv_likes)     // 좋아요 수 또는 조회수 표시용

        init {
            cardView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community_today_closet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position] // 이제 item은 TodayClosetItem 타입입니다.

        // Glide를 사용하여 게시물 이미지 로드 (frontImage 사용)
        Glide.with(holder.itemView.context)
            .load(item.frontImage) // TodayClosetItem의 frontImage 필드
            .placeholder(R.drawable.ic_insta)
            //.error(R.drawable.ic_error_placeholder) // 에러 시 보여줄 이미지 (ic_error_placeholder.xml 같은 드로어블 필요)
            .centerCrop()
            .into(holder.postImageView)

        // Glide를 사용하여 사용자 프로필 이미지 로드
        if (item.profileImage.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.profileImage) // TodayClosetItem의 profileImage 필드
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(holder.userProfileImageView)
        } else {
            holder.userProfileImageView.setImageResource(R.drawable.ic_profile)
        }

        holder.userNameTextView.text = "User ${item.todayClosetId}"

        // likeCount는 TodayClosetItem에 없으므로, viewCount를 사용하거나 서버 응답 변경 필요
        holder.likesTextView.text = item.viewCount.toString() // viewCount를 좋아요 수처럼 표시

        // 좋아요 아이콘은 현재 상태를 알 수 없으므로 기본 상태로 표시
        holder.heartIconImageView.setImageResource(R.drawable.ic_like_on) // 기본 아이콘
        holder.heartIconImageView.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.gray_dark))

        holder.cardView.contentDescription = "오늘의 옷장 게시물 ID ${item.postId}, 조회수 ${item.viewCount}"
    }

    override fun getItemCount(): Int = items.size

    // 데이터를 업데이트하는 함수 (Fragment에서 호출)
    fun updateData(newItems: List<TodayClosetItem>) {
        items = newItems
        notifyDataSetChanged() // 데이터 변경 알림
    }
}