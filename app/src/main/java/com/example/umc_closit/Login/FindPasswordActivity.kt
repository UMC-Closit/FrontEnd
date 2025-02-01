package com.example.umc_closit.Login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R
import android.content.Intent
import android.widget.Button


class FindPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findpassword)

        // 다음 버튼 클릭 이벤트
        val nextButton: Button = findViewById(R.id.btn_next2)
        nextButton.setOnClickListener {
            val intent = Intent(this, FindPassword2Activity::class.java)
            startActivity(intent)
        }
    }
}