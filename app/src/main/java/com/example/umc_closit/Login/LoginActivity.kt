package com.example.umc_closit.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R
import com.example.umc_closit.Community.TimelineActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 회원가입 버튼 클릭 이벤트
        val registerButton: Button = findViewById(R.id.btn_register)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }
        val loginButton: Button = findViewById(R.id.btn_login)
        loginButton.setOnClickListener {
            val intent = Intent(this, TimelineActivity::class.java)
            startActivity(intent)
            finish() // 현재 로그인 액티비티 종료
        }
        val FindIDButton: Button = findViewById(R.id.btn_find_id)
        FindIDButton.setOnClickListener {
            val intent = Intent(this, FindIDActivity::class.java)
            startActivity(intent)

        }
        val FindPasswordButton: Button = findViewById(R.id.btn_find_password)
        FindPasswordButton.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)

        }
    }
}