package com.example.umc_closit.ui.login.find

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.databinding.ActivityFindpassword2Binding
import com.example.umc_closit.ui.login.LoginActivity

class FindPassword2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityFindpassword2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindpassword2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다음 버튼 클릭 이벤트
        binding.btnNext2.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}