package com.example.umc_closit.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다음 버튼 클릭 이벤트
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, MakeIdActivity::class.java)
            startActivity(intent)
        }
    }
}