package com.example.umc_closit.ui.profile.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.data.entities.RecentItem
import com.example.umc_closit.data.remote.post.UserRecentPostDTO
import com.example.umc_closit.databinding.ItemRecentBinding

class RecentAdapter(
    private var items: List<UserRecentPostDTO>,
    private val screenWidth: Int,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<RecentAdapter.RecentViewHolder>() {

    inner class RecentViewHolder(private val binding: ItemRecentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserRecentPostDTO, itemWidth: Int) {
            Glide.with(binding.root.context)
                .load(item.thumbnail)
                .into(binding.ivRecentImage)

            val itemHeight = (itemWidth * 10) / 6
            binding.ivRecentImage.layoutParams.apply {
                width = itemWidth
                height = itemHeight
            }
            binding.ivRecentImage.requestLayout()

            binding.root.setOnClickListener {
                onItemClick(item.postId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = ItemRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val itemWidth = (screenWidth * 0.12).toInt()
        holder.bind(items[position], itemWidth)
    }

    fun updateItems(newItems: List<UserRecentPostDTO>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getPostIdList(): List<Int> {
        return items.map { it.postId }
    }

}
