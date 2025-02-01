package com.example.umc_closit.ui.upload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.databinding.ItemUploadBinding

class UploadAdapter(private val photos: List<Pair<Int, Int>>) :
    RecyclerView.Adapter<UploadAdapter.UploadViewHolder>() {

    class UploadViewHolder(val binding: ItemUploadBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadViewHolder {
        val binding = ItemUploadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UploadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UploadViewHolder, position: Int) {
        val (bigImageRes, smallImageRes) = photos[position]

        holder.binding.ivImageBig.setImageResource(bigImageRes) // ✅ 큰 이미지 설정
        holder.binding.ivImageSmall.setImageResource(smallImageRes) // ✅ 작은 이미지 설정
    }

    override fun getItemCount(): Int = photos.size
}