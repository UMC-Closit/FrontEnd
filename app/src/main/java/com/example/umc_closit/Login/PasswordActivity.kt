package com.example.umc_closit.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R

class PasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        // 설정 완료 버튼 클릭 이벤트
        val setCredentialsButton: Button = findViewById(R.id.btn_set_credentials)
        setCredentialsButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 스택 정리하여 로그인 화면으로 이동
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }
}