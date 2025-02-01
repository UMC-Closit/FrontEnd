package com.example.umc_closit.Community

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R
import com.example.umc_closit.ui.timeline.DetailActivity

class TodayClosetAdapter(private val itemList: List<String>) :
    RecyclerView.Adapter<TodayClosetAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_image_big) // 아이템 내부의 ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_double_picture, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 클릭 시 DetailActivity로 이동
        holder.imageView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("ITEM_POSITION", position) // 예: 인덱스 번호를 전달
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = itemList.size
}
