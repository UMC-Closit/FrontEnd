package com.example.umc_closit.ui.community.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.battle.TodayClosetItem

class CommunityBattlePostAdapter(
    private val items: List<TodayClosetItem>,
    private val onItemClick: (TodayClosetItem) -> Unit
) : RecyclerView.Adapter<CommunityBattlePostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.img_post)

        init {
            itemView.setOnClickListener {
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
        val item = items[position]

        Glide.with(holder.itemView.context)
            .load(item.frontImage)  // TodayClosetItem의 frontImage를 battle 카드 이미지로 사용
            .placeholder(R.drawable.ic_insta)
            .centerCrop()
            .into(holder.postImageView)
    }

    override fun getItemCount(): Int = items.size
}
