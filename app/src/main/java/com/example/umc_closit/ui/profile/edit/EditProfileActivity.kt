package com.example.umc_closit.ui.profile.edit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish() // 추후 화면 연결
        }

        // 변경하기 버튼, 추후 api 설정
        binding.btnUpdate.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val birth = binding.birthEditText.text.toString()
            val id = binding.idEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

        }
    }
}