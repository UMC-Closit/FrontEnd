package com.example.umc_closit.ui.community.challenge

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.battle.ChallengeBattlePreview
import com.example.umc_closit.data.remote.post.UserRecentPostDTO
import com.example.umc_closit.databinding.ItemBattle2Binding
import com.example.umc_closit.ui.community.challenge.NewChallengeDetailActivity

class NewChallengeAdapter(
    private val itemList: List<UserRecentPostDTO>,
    private val context: Context,
    private val challengeData: ChallengeBattlePreview
) : RecyclerView.Adapter<NewChallengeAdapter.ChallengeViewHolder>() {

    inner class ChallengeViewHolder(val binding: ItemBattle2Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = ItemBattle2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChallengeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        val post = itemList[position]

        // 썸네일 이미지 로드
        Glide.with(context)
            .load(post.thumbnail)
            .placeholder(R.drawable.img_gray_square)  // 로딩 중일 때 기본 이미지
            .error(R.drawable.img_gray_square)        // 로딩 실패 시 기본 이미지
            .centerCrop()
            .into(holder.binding.imageView)

        // 이미지 클릭 시 NewChallengeDetailActivity 로 이동
        holder.binding.imageView.setOnClickListener {
            val intent = Intent(context, NewChallengeDetailActivity::class.java).apply {
                putExtra("thumbnail_url", post.thumbnail)  // 썸네일 URL 전달
                putExtra("post_id", post.postId)           // postId 전달
                putExtra("challenge_data", challengeData)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = itemList.size
}
