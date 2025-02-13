package com.example.umc_closit.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.LoginRequest
import com.example.umc_closit.data.remote.LoginResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.databinding.ActivityLoginBinding
import com.example.umc_closit.ui.login.find.FindIDActivity
import com.example.umc_closit.ui.login.find.FindPasswordActivity
import com.example.umc_closit.ui.timeline.TimelineActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false // 비밀번호 표시 여부


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoginStatus() // 로그인 체크


        binding.btnLogin.setOnClickListener {
            val email = binding.passwordContainer.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        // 비밀번호 보기/숨기기 토글 기능
        binding.btnTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // 회원가입 버튼 클릭 이벤트
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 아이디 찾기 버튼 클릭 이벤트
        binding.btnFindId.setOnClickListener {
            val intent = Intent(this, FindIDActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 찾기 버튼 클릭 이벤트
        binding.btnFindPassword.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // 비밀번호 숨김 (기본 상태)
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.btnTogglePassword.setImageResource(R.drawable.ic_eye_off)
        } else {
            // 비밀번호 표시
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.btnTogglePassword.setImageResource(R.drawable.ic_eye)
        }
        isPasswordVisible = !isPasswordVisible
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email, password)

        RetrofitClient.authService.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("LOGIN_SUCCESS", "응답: $result")

                    if (result != null && result.isSuccess) {
                        val accessToken = result.result?.accessToken ?: ""
                        val refreshToken = result.result?.refreshToken ?: ""
                        val clositId = result.result?.clositId ?: ""

                        saveTokens(accessToken, refreshToken, clositId)
                        startActivity(Intent(this@LoginActivity, TimelineActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "로그인 실패: ${result?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (response.code() == 400) {
                        Toast.makeText(this@LoginActivity, "이메일과 비밀번호를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("LOGIN_ERROR", "서버 오류: ${response.code()}, 메시지: ${response.errorBody()?.string()}")
                        Toast.makeText(this@LoginActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LOGIN_ERROR", "네트워크 오류: ${t.message}")
                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun saveTokens(accessToken: String?, refreshToken: String?, clositId: String?) {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("accessToken", accessToken)
            putString("refreshToken", refreshToken)
            putString("clositId", clositId)
            putBoolean("isLoggedIn", true)
            apply()
        }
        Log.d("TOKEN_STORAGE", "토큰 및 clositId 저장 완료: accessToken=$accessToken, refreshToken=$refreshToken, clositId=$clositId")
    }



    // 자동 로그인 기능 추가
    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            Log.d("AUTO_LOGIN", "자동 로그인 진행 중...")
            startActivity(Intent(this, TimelineActivity::class.java))
            finish()
        }
    }
}