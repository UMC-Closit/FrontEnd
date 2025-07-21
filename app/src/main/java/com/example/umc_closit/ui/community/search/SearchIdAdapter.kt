package com.example.umc_closit.ui.community.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

class SearchIdAdapter(private val items: List<SearchIdItem>) :
    RecyclerView.Adapter<SearchIdAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.img_profile)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvViews: TextView = itemView.findViewById(R.id.tv_views)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result_id, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imgProfile.setImageResource(item.profileResId)
        holder.tvName.text = item.name
        holder.tvViews.text = "${item.views} 조회수"
    }

    override fun getItemCount(): Int = items.size
}