package com.example.umc_closit.ui.login.find

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.databinding.ActivityFindidBinding

class FindIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindidBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()  // 최신 방식 사용
        }

        // 이메일 인증 요청 버튼 클릭
        binding.btnCheckEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: 이메일 인증번호 요청 API 호출 로직
                Toast.makeText(this, "인증번호를 전송했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 다음 단계로 이동
        binding.btnNext.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val code = binding.etConfirmNumber.text.toString().trim()

            if (email.isEmpty() || code.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: 인증번호 확인 로직 필요
                // 인증번호 확인 성공 시 다음 화면으로 이동
                val intent = Intent(this, FindID2Activity::class.java)
                startActivity(intent)
            }
        }
    }
}
