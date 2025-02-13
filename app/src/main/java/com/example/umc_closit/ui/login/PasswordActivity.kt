package com.example.umc_closit.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.MainActivity
import com.example.umc_closit.data.remote.RegisterRequest
import com.example.umc_closit.data.remote.RegisterResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.databinding.ActivityPasswordBinding
import com.example.umc_closit.ui.timeline.TimelineActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordBinding
    private var passwordValidationFailed = false
    private var confirmPasswordValidationFailed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name") ?: ""
        val userId = intent.getStringExtra("userId") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val birthDate = intent.getStringExtra("birthDate") ?: ""

        // 초기 버튼 비활성화 및 오류 메시지 숨김
        binding.btnSetCredentials.isEnabled = false
        binding.tvErrorPw.visibility = View.GONE
        binding.tvErrorCfpw.visibility = View.GONE

        setupTextWatchers()
        setupFocusListeners()

        binding.btnSetCredentials.setOnClickListener {
            validateAndRegister(name, userId, email, birthDate)
        }

        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupTextWatchers() {
        binding.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = binding.etNewPassword.text.toString().trim()
                if (isValidPassword(password)) {
                    passwordValidationFailed = false
                    binding.tvErrorPw.visibility = View.GONE
                }
                validateInputs()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = binding.etNewPassword.text.toString().trim()
                val confirmPassword = binding.etConfirmPassword.text.toString().trim()
                if (confirmPassword.isNotEmpty() && password == confirmPassword) {
                    confirmPasswordValidationFailed = false
                    binding.tvErrorCfpw.visibility = View.GONE
                }
                validateInputs()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFocusListeners() {
        binding.etNewPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // 포커스를 잃으면 유효성 검증 실행
                val password = binding.etNewPassword.text.toString().trim()
                if (!isValidPassword(password)) {
                    passwordValidationFailed = true
                    binding.tvErrorPw.visibility = View.VISIBLE
                }
                validateInputs()
            } else if (passwordValidationFailed) {
                binding.tvErrorPw.visibility = View.VISIBLE // 포커스를 다시 얻어도 유지
            }
        }

        binding.etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // 포커스를 잃으면 유효성 검증 실행
                val password = binding.etNewPassword.text.toString().trim()
                val confirmPassword = binding.etConfirmPassword.text.toString().trim()
                if (confirmPassword.isBlank() || password != confirmPassword) {
                    confirmPasswordValidationFailed = true
                    binding.tvErrorCfpw.visibility = View.VISIBLE
                }
                validateInputs()
            } else if (confirmPasswordValidationFailed) {
                binding.tvErrorCfpw.visibility = View.VISIBLE // 포커스를 다시 얻어도 유지
            }
        }
    }

    private fun validateInputs() {
        val password = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        val isAllValid = password.isNotEmpty() &&
                confirmPassword.isNotEmpty() &&
                !passwordValidationFailed &&
                !confirmPasswordValidationFailed

        binding.btnSetCredentials.isEnabled = isAllValid
    }

    private fun validateAndRegister(name: String, userId: String, email: String, birthDate: String) {
        val password = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        var isValid = true

        binding.tvErrorPw.visibility = View.GONE
        binding.tvErrorCfpw.visibility = View.GONE

        if (!isValidPassword(password)) {
            binding.tvErrorPw.text = "비밀번호는 최소 8자리 이상이어야 합니다"
            binding.tvErrorPw.visibility = View.VISIBLE
            isValid = false
        }

        if (confirmPassword.isBlank() || password != confirmPassword) {
            binding.tvErrorCfpw.text = "비밀번호가 일치하지 않습니다"
            binding.tvErrorCfpw.visibility = View.VISIBLE
            isValid = false
        }

        if (!isValid) return

        // 모든 조건 충족 시 회원가입 API 호출
        registerUser(name, userId, email, birthDate)
    }

    private fun registerUser(name: String, userId: String, email: String, birthDate: String) {
        val password = binding.etNewPassword.text.toString().trim()
        val request = RegisterRequest(
            name = name,
            email = email,
            password = password,
            clositId = userId,
            birth = birthDate,
            profileImage = "android.resource://com.example.umc_closit/drawable/img_profile_default"
        )

        // 요청 데이터 로그 출력
        Log.d("API_REQUEST", "보내는 데이터: $request")


        RetrofitClient.authService.registerUser(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                Log.d("API_RESPONSE", "응답 코드: ${response.code()}")

                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("API_RESPONSE", "응답 본문: $result")

                    if (result != null && result.isSuccess) {
                        startActivity(Intent(this@PasswordActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@PasswordActivity, "회원가입 실패: ${result?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API_ERROR", "서버 오류: ${response.code()}, 메시지: ${response.errorBody()?.string()}")
                    Toast.makeText(this@PasswordActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("API_ERROR", "네트워크 오류: ${t.message}")
                Toast.makeText(this@PasswordActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 비밀번호 유효성 검사 (최소 8자 이상)
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }
}