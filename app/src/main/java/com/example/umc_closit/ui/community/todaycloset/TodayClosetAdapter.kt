package com.example.umc_closit.ui.community.todaycloset

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.databinding.ItemDoublePictureBinding
import com.example.umc_closit.ui.timeline.detail.DetailActivity

class TodayClosetAdapter(private val itemList: List<String>) :
    RecyclerView.Adapter<TodayClosetAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDoublePictureBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDoublePictureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 클릭 시 DetailActivity로 이동
        holder.binding.ivImageBig.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("ITEM_POSITION", position) // 예: 인덱스 번호를 전달
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = itemList.size
}