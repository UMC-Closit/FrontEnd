package com.example.umc_closit.ui.login.find

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.databinding.ActivityFindpasswordBinding

class FindPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindpasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다음 버튼 클릭 이벤트
        binding.btnNext2.setOnClickListener {
            val intent = Intent(this, FindPassword2Activity::class.java)
            startActivity(intent)
        }
    }
}