package com.example.umc_closit.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R

class MakeIdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_makeid)

        // continue 버튼 클릭 이벤트
        val setCredentialsButton: Button = findViewById(R.id.btn_next)
        setCredentialsButton.setOnClickListener {
            val intent = Intent(this, PasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 스택 정리하여 다음 화면으로 이동
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }
}