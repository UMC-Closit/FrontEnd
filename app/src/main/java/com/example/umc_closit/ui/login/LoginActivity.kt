package com.example.umc_closit.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.auth.LoginRequest
import com.example.umc_closit.data.remote.auth.LoginResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.auth.SocialLoginRequest
import com.example.umc_closit.data.remote.auth.SocialLoginResponse
import com.example.umc_closit.databinding.ActivityLoginBinding
import com.example.umc_closit.ui.login.find.FindIDActivity
import com.example.umc_closit.ui.login.find.FindPasswordActivity
import com.example.umc_closit.ui.timeline.TimelineActivity
import com.example.umc_closit.utils.TokenUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false // 비밀번호 표시 여부

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoginStatus() // 자동 로그인 체크

        // 1. GoogleSignInOptions 및 Client 초기화
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 2. 구글 로그인 아이콘 클릭 리스너
        binding.googleIcon.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // 3. 일반 로그인 버튼
        binding.btnLogin.setOnClickListener {
            val email = binding.passwordContainer.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        // 4. 비밀번호 보기/숨기기 토글
        binding.btnTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // 5. 회원가입, 아이디/비밀번호 찾기 버튼
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnFindId.setOnClickListener {
            startActivity(Intent(this, FindIDActivity::class.java))
        }
        binding.btnFindPassword.setOnClickListener {
            startActivity(Intent(this, FindPasswordActivity::class.java))
        }
    }

    // 구글 로그인 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                sendIdTokenToServer(idToken)
            } else {
                Toast.makeText(this, "구글 토큰이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "구글 로그인 실패: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendIdTokenToServer(idToken: String) {
        val api = { RetrofitClient.authService.socialLogin("GOOGLE", SocialLoginRequest(idToken)) }
        api().enqueue(object : Callback<SocialLoginResponse> {
            override fun onResponse(call: Call<SocialLoginResponse>, response: Response<SocialLoginResponse>) {
                val code = response.code()
                val raw = response.raw()
                Log.d("SOCIAL_LOGIN", "HTTP $code ${raw.request.url}")
                Log.d("SOCIAL_LOGIN", "headers=${response.headers()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("SOCIAL_LOGIN", "body=$body")
                    if (body?.isSuccess == true && body.result != null) {
                        val result = body.result
                        // TokenUtils.saveTokens(this@LoginActivity, result.accessToken, result.refreshToken, result.userId)
                        Toast.makeText(this@LoginActivity, "서버 로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, TimelineActivity::class.java))
                        finish()
                    } else {
                        Log.e("SOCIAL_LOGIN", "isSuccess=${body?.isSuccess}, code=${body?.code}, message=${body?.message}, result=${body?.result}")
                        Toast.makeText(this@LoginActivity, "서버 로그인 실패: ${body?.message ?: "no message"}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val err = response.errorBody()?.string()
                    Log.e("SOCIAL_LOGIN", "errorBody=$err")
                    Toast.makeText(this@LoginActivity, "서버 오류: $code", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<SocialLoginResponse>, t: Throwable) {
                Log.e("SOCIAL_LOGIN", "network failure", t)
                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // 숨김 상태
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.btnTogglePassword.setImageResource(R.drawable.ic_eye_off)
        } else {
            // 표시 상태
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.btnTogglePassword.setImageResource(R.drawable.ic_eye)
        }

        val typeface = ResourcesCompat.getFont(binding.root.context, R.font.noto_regular)
        binding.etPassword.typeface = typeface

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

                        // 토큰 및 Closit ID 저장
                        TokenUtils.saveTokens(this@LoginActivity, accessToken, refreshToken, clositId)

                        // 로그인 성공 후 타임라인 이동
                        startActivity(Intent(this@LoginActivity, TimelineActivity::class.java))
                        Log.d("TOKEN_DEBUG", "로그인 성공 후 AccessToken: $accessToken")
                        Log.d("TOKEN_DEBUG", "로그인 성공 후 RefreshToken: $refreshToken")
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "로그인 실패: ${result?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    when (response.code()) {
                        400 -> Toast.makeText(this@LoginActivity, "이메일과 비밀번호를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
                        404 -> Toast.makeText(this@LoginActivity, "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show()
                        else -> {
                            Log.e("LOGIN_ERROR", "서버 오류: ${response.code()}, 메시지: ${response.errorBody()?.string()}")
                            Toast.makeText(this@LoginActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LOGIN_ERROR", "네트워크 오류: ${t.message}")
                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 자동 로그인 기능 추가
    private fun checkLoginStatus() {
        val isLoggedIn = TokenUtils.isLoggedIn(this)

        if (isLoggedIn) {
            Log.d("AUTO_LOGIN", "자동 로그인 진행 중...")
            startActivity(Intent(this, TimelineActivity::class.java))
            finish()
        }
    }
}