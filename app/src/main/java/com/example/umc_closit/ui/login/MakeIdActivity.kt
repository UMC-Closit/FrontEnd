package com.example.umc_closit.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.umc_closit.R
import com.example.umc_closit.databinding.ActivityMakeidBinding

class MakeIdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakeidBinding
    private var emailValidationFailed = false
    private var birthDateValidationFailed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnCheckId.setOnClickListener{

        }

        // 초기에 버튼 비활성화 및 오류 메시지 숨김
        binding.btnNext.isEnabled = false
        binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_dark)
        binding.tvErrorEmail.visibility = View.GONE
        binding.tvErrorBirthdate.visibility = View.GONE

        setupTextWatchers()
        setupFocusListeners()

        binding.btnNext.setOnClickListener {
            val name = binding.etName.text.toString()
            val userId = binding.etId.text.toString()
            val email = binding.etEmail.text.toString()
            val birthDate = binding.etBirthdate.text.toString()

            // 입력값 검증
            if (name.isBlank() || userId.isBlank() || email.isBlank() || birthDate.isBlank()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PasswordActivity::class.java).apply {
                putExtra("name", name)
                putExtra("userId", userId)
                putExtra("email", email)
                putExtra("birthDate", birthDate)
            }
            startActivity(intent)
        }
    }

    private fun setupTextWatchers() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.etEmail.text.toString().trim()
                if (isValidEmail(email)) {
                    emailValidationFailed = false
                    binding.tvErrorEmail.visibility = View.GONE
                }
                validateInputs()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etBirthdate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val birthDate = binding.etBirthdate.text.toString().trim()
                if (isValidBirthDate(birthDate)) {
                    birthDateValidationFailed = false
                    binding.tvErrorBirthdate.visibility = View.GONE
                }
                validateInputs()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFocusListeners() {
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // 포커스를 잃으면 유효성 검증 실행
                val email = binding.etEmail.text.toString().trim()
                if (!isValidEmail(email)) {
                    emailValidationFailed = true
                    binding.tvErrorEmail.visibility = View.VISIBLE
                }
                validateInputs()
            } else if (emailValidationFailed) {
                binding.tvErrorEmail.visibility = View.VISIBLE // 포커스를 다시 얻어도 유지
            }
        }

        binding.etBirthdate.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // 포커스를 잃으면 유효성 검증 실행
                val birthDate = binding.etBirthdate.text.toString().trim()
                if (!isValidBirthDate(birthDate)) {
                    birthDateValidationFailed = true
                    binding.tvErrorBirthdate.visibility = View.VISIBLE
                }
                validateInputs()
            } else if (birthDateValidationFailed) {
                binding.tvErrorBirthdate.visibility = View.VISIBLE // 포커스를 다시 얻어도 유지
            }
        }
    }

    private fun validateInputs() {
        val name = binding.etName.text.toString().trim()
        val userId = binding.etId.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val birthDate = binding.etBirthdate.text.toString().trim()

        val isAllValid = name.isNotEmpty() && userId.isNotEmpty() &&
                email.isNotEmpty() && birthDate.isNotEmpty() &&
                !emailValidationFailed && !birthDateValidationFailed

        binding.btnNext.isEnabled = isAllValid

        // 버튼 색상 변경
        val color = if (isAllValid) R.color.black else R.color.gray_dark
        binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(this, color)
    }

    // 이메일 유효성 검사
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailPattern.toRegex())
    }

    // 생년월일 유효성 검사 (YYYY-MM-DD 형식)
    private fun isValidBirthDate(birthDate: String): Boolean {
        val datePattern = "^\\d{4}-\\d{2}-\\d{2}$"
        return birthDate.matches(datePattern.toRegex())
    }
}