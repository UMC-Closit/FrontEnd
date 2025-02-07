package com.example.umc_closit.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.databinding.ActivityLoginBinding
import com.example.umc_closit.ui.login.find.FindIDActivity
import com.example.umc_closit.ui.login.find.FindPasswordActivity
import com.example.umc_closit.ui.timeline.TimelineActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 버튼 클릭 이벤트
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 클릭 이벤트
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, TimelineActivity::class.java)
            startActivity(intent)
            finish() // 현재 로그인 액티비티 종료
        }

        // 아이디 찾기 버튼 클릭 이벤트
        binding.btnFindId.setOnClickListener {
            val intent = Intent(this, FindIDActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 찾기 버튼 클릭 이벤트
        binding.btnFindPassword.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}