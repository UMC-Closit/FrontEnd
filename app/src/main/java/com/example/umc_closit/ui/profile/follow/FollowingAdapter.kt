package com.example.umc_closit.ui.follow

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.data.remote.profile.Follow
import com.example.umc_closit.databinding.ItemFollowingListBinding
import com.example.umc_closit.ui.timeline.TimelineActivity

class FollowingAdapter(private val followingItems: MutableList<Follow>) :
    RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder>() {

    class FollowingViewHolder(val binding: ItemFollowingListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingViewHolder {
        val binding = ItemFollowingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowingViewHolder, position: Int) {
        val item = followingItems[position]

        with(holder.binding) {
            Glide.with(holder.itemView.context).load(item.profileImage).circleCrop().into(holder.binding.ivUserProfile)
            tvUserName.text = item.name
            tvUserId.text = "@${item.clositId}"

            // 프로필 사진 클릭 시 해당 유저의 프로필로 이동
            ivUserProfile.setOnClickListener {
                val intent = Intent(holder.itemView.context, TimelineActivity::class.java)
                intent.putExtra("profileUserClositId", item.clositId)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return followingItems.size
    }

    fun updateFollowingItems(newItems: List<Follow>) {
        this.followingItems.addAll(newItems)
        notifyDataSetChanged()
    }
}