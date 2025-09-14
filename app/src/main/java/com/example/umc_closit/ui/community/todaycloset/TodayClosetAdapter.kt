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
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.timeline.LikeResponse
import com.example.umc_closit.utils.TokenUtils
import android.widget.Toast

class TodayClosetAdapter : RecyclerView.Adapter<TodayClosetAdapter.ViewHolder>() {

    private val itemList = mutableListOf<TodayClosetItem>()

    private val isLikedMap = mutableMapOf<Int, Boolean>()
    private val likeCountMap = mutableMapOf<Int, Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val frontImage: ImageView = view.findViewById(R.id.img_front)
        val backImage: ImageView = view.findViewById(R.id.img_back)
        val profileImage: ImageView = view.findViewById(R.id.iv_user_profile)
        val likeCountNum: TextView = view.findViewById(R.id.like_count_num)
        val ivLike: ImageView = view.findViewById(R.id.iv_like)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_today_closet_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        val postId = item.postId
        val isLiked = isLikedMap[postId] ?: false
        val likeCount = likeCountMap[postId] ?: 0

        holder.ivLike.setImageResource(if (isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off)
        holder.likeCountNum.text = likeCount.toString()

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

        holder.ivLike.setOnClickListener {
            val currentlyLiked = isLikedMap[postId] ?: false
            if (currentlyLiked) {
                val call = { RetrofitClient.timelineService.removeLike(postId) }
                TokenUtils.handleTokenRefresh(
                    call = call(),
                    onSuccess = { resp: LikeResponse ->
                        if (resp.isSuccess) {
                            isLikedMap[postId] = resp.result.isLiked
                            likeCountMap[postId] = resp.result.likeCount
                            notifyItemChanged(holder.bindingAdapterPosition)
                        }
                    },
                    onFailure = { /* 토스트 등 */ },
                    context = holder.itemView.context
                )
            } else {
                val call = { RetrofitClient.timelineService.addLike(postId) }
                TokenUtils.handleTokenRefresh(
                    call = call(),
                    onSuccess = { resp: LikeResponse ->
                        if (resp.isSuccess) {
                            isLikedMap[postId] = resp.result.isLiked
                            likeCountMap[postId] = resp.result.likeCount
                            notifyItemChanged(holder.bindingAdapterPosition)
                        }
                    },
                    onFailure = { /* 토스트 등 */ },
                    context = holder.itemView.context
                )
            }
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
