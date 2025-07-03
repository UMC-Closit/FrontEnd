package com.example.umc_closit.ui.community.adapter // 실제 패키지 경로로 수정

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

// "배틀 게시판" 아이템 데이터 클래스
data class BattlePostItem(
    val imageResId: Int, // 표시할 이미지의 리소스 ID
    val battleId: String  // 배틀 고유 ID
)

class CommunityBattlePostAdapter(
    private val items: List<BattlePostItem>,
    private val onItemClick: (BattlePostItem) -> Unit
) : RecyclerView.Adapter<CommunityBattlePostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageButton: ImageButton = itemView.findViewById(R.id.ib_community_battle_post_item) // item_community_battle_post.xml의 ID

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
            .inflate(R.layout.item_community_battle_post, parent, false) // 해당 아이템 레이아웃 사용
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageButton.setImageResource(item.imageResId)
    }

    override fun getItemCount(): Int = items.size
}