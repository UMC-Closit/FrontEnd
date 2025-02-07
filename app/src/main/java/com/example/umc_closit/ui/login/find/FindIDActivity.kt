package com.example.umc_closit.ui.login.find

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.databinding.ActivityFindidBinding

class FindIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindidBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다음 버튼 클릭 이벤트
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, FindID2Activity::class.java)
            startActivity(intent)
        }
    }
}