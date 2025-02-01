package com.example.umc_closit.Login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R
import android.content.Intent
import android.widget.Button


class FindIDActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findid)

        // 다음 버튼 클릭 이벤트
        val nextButton: Button = findViewById(R.id.btn_next)
        nextButton.setOnClickListener {
            val intent = Intent(this, FindID2Activity::class.java)
            startActivity(intent)
        }
    }
}