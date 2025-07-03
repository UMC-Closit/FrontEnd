package com.example.umc_closit.ui.community.adapter // 실제 패키지 경로로 수정

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

// "오늘의 옷장" 아이템 데이터 클래스
data class TodayClosetItem(
    val imageResId: Int, // 표시할 이미지의 리소스 ID
    val postId: String    // 게시물 고유 ID (클릭 시 상세 정보 조회 등에 사용)
)

class CommunityTodayClosetAdapter(
    private val items: List<TodayClosetItem>,
    private val onItemClick: (TodayClosetItem) -> Unit
) : RecyclerView.Adapter<CommunityTodayClosetAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageButton: ImageButton = itemView.findViewById(R.id.ib_community_battle_post_item)

        init {
            imageButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community_today_closet, parent, false) // 해당 아이템 레이아웃 사용
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageButton.setImageResource(item.imageResId)
        // 필요시 holder.imageButton.contentDescription = "오늘의 옷장 ${item.postId}" 등으로 설정
    }

    override fun getItemCount(): Int = items.size
}