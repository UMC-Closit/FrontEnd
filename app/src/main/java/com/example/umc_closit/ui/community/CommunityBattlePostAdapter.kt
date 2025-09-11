package com.example.umc_closit.ui.community.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.battle.BattlePreview

class CommunityBattlePostAdapter(
    private var items: List<BattlePreview>,
    private val onItemClick: (BattlePreview) -> Unit
) : RecyclerView.Adapter<CommunityBattlePostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.img_post)
        val userIdTextView: TextView = itemView.findViewById(R.id.tv_user_id)
        val likeCountTextView: TextView = itemView.findViewById(R.id.tv_likes)

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
            .inflate(R.layout.item_community_battle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // firstPostFrontImage를 배틀 카드 이미지로 사용
        Glide.with(holder.itemView.context)
            .load(item.firstPostFrontImage)
            .placeholder(R.drawable.img_gray_square)
            .error(R.drawable.img_gray_square)
            .centerCrop()
            .into(holder.postImageView)

        // firstClositId 표시
        holder.userIdTextView.text = item.firstClositId

        // likeCount 표시
        holder.likeCountTextView.text = item.likeCount.toString()
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<BattlePreview>) {
        items = newItems
        notifyDataSetChanged()
    }
}
