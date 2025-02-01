package com.example.umc_closit.ui.profile.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.data.RecentItem
import com.example.umc_closit.databinding.ItemRecentBinding

class RecentAdapter(
    private val items: List<RecentItem>,
    private val screenWidth: Int // 화면 너비를 전달받음
) : RecyclerView.Adapter<RecentAdapter.RecentViewHolder>() {

    class RecentViewHolder(private val binding: ItemRecentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecentItem, itemWidth: Int) {
            // 이미지와 텍스트 설정
            binding.ivRecentImage.setImageResource(item.imageResId)

            // 동적으로 크기 설정 (비율 10:6 적용)
            val itemHeight = (itemWidth * 10) / 6 // 비율 계산
            binding.ivRecentImage.layoutParams = binding.ivRecentImage.layoutParams.apply {
                width = itemWidth
                height = itemHeight
            }
            binding.ivRecentImage.requestLayout() // 크기 갱신
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = ItemRecentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val itemWidth = (screenWidth * 0.12).toInt() // 화면 너비의 10% 계산
        holder.bind(items[position], itemWidth)
    }

    override fun getItemCount(): Int = items.size
}
