package com.example.umc_closit.ui.community.challenge

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.battle.ChallengeBattlePreview
import com.example.umc_closit.databinding.ItemChallengeBinding

class ChallengeAdapter(
    private val challengeList: List<ChallengeBattlePreview>,
    private val context: Context
) : RecyclerView.Adapter<ChallengeAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemChallengeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(challenge: ChallengeBattlePreview) {

            // Glide를 이용해 이미지 로드
            Glide.with(binding.root.context)
                .load(challenge.firstPostFrontImage)
                .placeholder(R.drawable.image_background)
                .into(binding.leftItem)

            // Challenge 제목 설정
            binding.challengeTitle.text = challenge.title

            // 유저네임과 프로필 이미지 설정
            binding.userName.text = challenge.firstClositId
            Glide.with(binding.root.context)
                .load(challenge.firstProfileImage)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(binding.profileImage)

            // 오른쪽 "도전하기" 카드 클릭 이벤트
            binding.rightItem.root.setOnClickListener {
                val intent = Intent(binding.root.context, NewChallengeActivity::class.java)
                intent.putExtra("challenge_data", challenge)
                binding.root.context.startActivity(intent)
                Toast.makeText(binding.root.context, "도전하기!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChallengeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ❌ 잘못된 context 전달 제거
        holder.bind(challengeList[position])
    }

    override fun getItemCount(): Int = challengeList.size
}
