package com.example.umc_closit.Community

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R

class NewChallengeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_challenge_detail)

        // 뒤로 가기 버튼 클릭 시 현재 액티비티 종료
        val backButton: ImageView = findViewById(R.id.iv_back)
        backButton.setOnClickListener {
            finish() // 현재 액티비티 종료 -> 이전 페이지로 이동
        }
    }
}