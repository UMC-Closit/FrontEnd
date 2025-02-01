package com.example.umc_closit.Community

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.umc_closit.R
import com.example.umc_closit.databinding.ActivityMakechallengeBinding

class NewChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMakechallengeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakechallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 설정 (가로 3개 그리드)
        val itemList = List(21) { R.drawable.img_gray_square } // 모든 항목을 동일한 이미지로 설정

        val adapter = NewChallengeAdapter(itemList, this)
        binding.challengeRecyclerView.layoutManager = GridLayoutManager(this, 3) // 한 줄에 3개
        binding.challengeRecyclerView.adapter = adapter

        binding.createButton.setOnClickListener {
            onBackPressed()  // 뒤로 가기
        }
    }
}