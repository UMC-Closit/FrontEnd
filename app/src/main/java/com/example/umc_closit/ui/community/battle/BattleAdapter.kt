package com.example.umc_closit.ui.community.battle

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.post.UserRecentPostDTO
import com.example.umc_closit.databinding.ItemBattle2Binding
import com.example.umc_closit.ui.community.battle.NewBattleDetailActivity
import com.example.umc_closit.databinding.ItemBattleBinding

class BattleAdapter(
    private val itemList: List<UserRecentPostDTO>,
    private val context: Context
) : RecyclerView.Adapter<BattleAdapter.BattleViewHolder>() {

    inner class BattleViewHolder(val binding: ItemBattle2Binding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BattleViewHolder {
        val binding = ItemBattle2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BattleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BattleViewHolder, position: Int) {
        val post = itemList[position]
        Glide.with(context)
            .load(post.thumbnail)
            .placeholder(R.drawable.img_gray_square)  // 로딩 중일 때 기본 이미지
            .error(R.drawable.img_gray_square)        // 로딩 실패 시 기본 이미지
            .centerCrop()
            .into(holder.binding.imageView)           // imageView는 item_battle.xml에 있는 이미지 뷰

        holder.binding.imageView.setOnClickListener {
            val intent = Intent(context, NewBattleDetailActivity::class.java).apply {
                putExtra("thumbnail_url", post.thumbnail)  // 클릭한 썸네일 URL 전달
                putExtra("post_id", post.postId)  //  postId 전달
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = itemList.size
}