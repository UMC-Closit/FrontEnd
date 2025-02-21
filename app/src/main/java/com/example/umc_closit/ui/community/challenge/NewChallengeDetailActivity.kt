package com.example.umc_closit.ui.community.challenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.data.remote.battle.ChallengeBattlePreview
import com.example.umc_closit.databinding.ActivityNewChallengeDetailBinding

class NewChallengeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewChallengeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewChallengeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val thumbnailUrl = intent.getStringExtra("thumbnail_url")
        val postId = intent.getIntExtra("post_id", -1)
        val challengeData = intent.getParcelableExtra<ChallengeBattlePreview>("challenge_data")

        // 뒤로 가기 버튼 클릭 시 현재 액티비티 종료
        binding.ivBack.setOnClickListener {
            finish() // 현재 액티비티 종료 -> 이전 페이지로 이동
        }
    }
}