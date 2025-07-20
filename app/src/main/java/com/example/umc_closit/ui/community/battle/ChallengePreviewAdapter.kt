package com.example.umc_closit.ui.community.battle

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.battle.ChallengeBattlePreview
import com.example.umc_closit.databinding.ItemChallengePreviewBinding
import com.example.umc_closit.ui.community.challenge.NewChallengeActivity
import com.example.umc_closit.utils.FileUtils

class ChallengePreviewAdapter(
    private val challengeList: List<ChallengeBattlePreview>,
    private val context: Context
) : RecyclerView.Adapter<ChallengePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemChallengePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(challenge: ChallengeBattlePreview) {
            // 이미지 로딩
            Glide.with(binding.root.context)
                .load(challenge.firstPostFrontImage)
                .placeholder(R.drawable.image_background)
                .into(binding.ivImageBig)

            Glide.with(binding.root.context)
                .load(challenge.firstPostBackImage)
                .placeholder(R.drawable.image_background)
                .into(binding.ivImageSmall)

            var isFrontImageBig = true
            val fakeTagContainer = ConstraintLayout(binding.root.context)

            // 이미지 스왑 처리
            binding.ivImageSmall.setOnClickListener {
                FileUtils.swapImagesWithTagEffect(
                    bigImageView = binding.ivImageBig,
                    smallImageView = binding.ivImageSmall,
                    tagContainer = fakeTagContainer
                ) {
                    isFrontImageBig = !isFrontImageBig
                }
            }

            // 클릭 시 NewChallengeActivity로 이동
            binding.clChallengeWrapper.setOnClickListener {
                val intent = Intent(binding.root.context, NewChallengeActivity::class.java)
                intent.putExtra("challenge_data", challenge)
                binding.root.context.startActivity(intent)
                Toast.makeText(binding.root.context, "도전하기!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChallengePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams
        val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
        layoutParams.width = screenWidth / 9 * 2
        holder.itemView.layoutParams = layoutParams

        holder.bind(challengeList[position])
    }
    override fun getItemCount(): Int = challengeList.size
}