package com.example.umc_closit.ui.community.todaycloset

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.battle.TodayClosetItem
import com.example.umc_closit.ui.timeline.detail.DetailActivity

class TodayClosetAdapter : RecyclerView.Adapter<TodayClosetAdapter.ViewHolder>() {

    private val itemList = mutableListOf<TodayClosetItem>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val frontImage: ImageView = view.findViewById(R.id.img_front)
        val backImage: ImageView = view.findViewById(R.id.img_back)
        val profileImage: ImageView = view.findViewById(R.id.iv_user_profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_today_closet_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        // 전면 사진 로드
        Glide.with(holder.itemView.context)
            .load(item.frontImage)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.frontImage)

        // 후면 사진 로드
        Glide.with(holder.itemView.context)
            .load(item.backImage)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.backImage)

        // 프로필 사진 로드
        Glide.with(holder.itemView.context)
            .load(item.profileImage)
            .placeholder(R.drawable.img_profile_default)
            .circleCrop()
            .into(holder.profileImage)


        // 아이템 클릭 시 상세 화면으로 이동
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("postId", item.postId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun submitList(list: List<TodayClosetItem>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }

    fun addItems(list: List<TodayClosetItem>) {
        val currentSize = itemList.size
        itemList.addAll(list)
        notifyItemRangeInserted(currentSize, list.size)
    }
}
