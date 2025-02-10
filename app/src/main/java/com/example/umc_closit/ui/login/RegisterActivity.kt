package com.example.umc_closit.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.umc_closit.R
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

        // 뒤로가기 버튼 클릭 이벤트
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }


        // 초기 버튼 비활성화 & 회색 설정
        binding.btnNext.isEnabled = false
        binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_dark)

        setupCheckListeners()

    }

    private fun setupCheckListeners() {
        val checkBoxes = listOf(binding.chkRegister1, binding.chkRegister2, binding.chkRegister3)

        val checkListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            validateChecks()
        }

        // 모든 체크박스에 리스너 등록
        checkBoxes.forEach { it.setOnCheckedChangeListener(checkListener) }
    }

    private fun validateChecks() {
        val allChecked = binding.chkRegister1.isChecked &&
                binding.chkRegister2.isChecked &&
                binding.chkRegister3.isChecked

        binding.btnNext.isEnabled = allChecked

        // 버튼 색상 변경
        if (allChecked) {
            binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
        } else {
            binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_dark)
        }
    }
}