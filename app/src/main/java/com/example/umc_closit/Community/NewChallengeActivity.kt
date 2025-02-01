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
        binding = ActivityMakechallengeBinding.inflate(layoutInflater) // 올바른 ViewBinding 클래스 사용
        setContentView(binding.root)

        // RecyclerView 설정 (가로 3개 그리드)
        val itemList = List(21) { R.drawable.img_gray_square } // 더미 이미지 리스트

        val adapter = BattleAdapter(itemList)
        binding.makeChallengeRecyclerView.layoutManager = GridLayoutManager(this, 3) // 한 줄에 3개
        binding.makeChallengeRecyclerView.adapter = adapter

        binding.createButton.setOnClickListener {
            onBackPressed()  // 뒤로 가기
        }
    }
}
