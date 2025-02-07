package com.example.umc_closit.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.databinding.ActivityMakeidBinding

class MakeIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMakeidBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // continue 버튼 클릭 이벤트
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, PasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 스택 정리하여 다음 화면으로 이동
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }
}