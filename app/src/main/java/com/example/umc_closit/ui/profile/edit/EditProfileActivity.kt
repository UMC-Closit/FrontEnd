package com.example.umc_closit.ui.profile.edit

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.EditProfileRequest
import com.example.umc_closit.data.remote.profile.ProfileUserResponse
import com.example.umc_closit.databinding.ActivityEditProfileBinding
import com.example.umc_closit.utils.TokenUtils
import java.text.SimpleDateFormat
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var loggedInUserClositId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences에서 clositId 가져오기
        val sp = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        loggedInUserClositId = sp.getString("clositId", "") ?: ""

        // 뒤로가기 버튼
        binding.ivBack.setOnClickListener {
            finish()
        }

        // 변경하기 버튼 클릭 리스너
        binding.btnUpdate.setOnClickListener {
            updateProfile()
        }


        // 초기화 - 현재 유저 정보 불러오기
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val apiCall = {
            RetrofitClient.profileService.getUserProfile(loggedInUserClositId)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    val user = response.result
                    binding.etName.setText(user.name)
                    binding.etBirth.setText(formatDate(user.birth))
                    binding.idEditText.setText(user.clositId)
                    binding.emailEditText.setText(user.email)
                } else {
                    Toast.makeText(this, "유저 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }

    private fun updateProfile() {
        val name = binding.etName.text.toString()
        val birth = binding.etBirth.text.toString()
        val email = binding.emailEditText.text.toString()
        val currentPassword = binding.passwordEditText.text.toString()
        val password = binding.newPasswordEditText.text.toString()

        if (name.isBlank() || birth.isBlank() || email.isBlank() || currentPassword.isBlank() || password.isBlank()) {
            Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val request = EditProfileRequest(
            name = name,
            currentPassword = currentPassword,
            password = password,
            birth = birth
        )

        val apiCall = {
            RetrofitClient.profileService.updateUserProfile(request)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response: ProfileUserResponse ->
                if (response.isSuccess) {
                    Toast.makeText(this, "프로필 정보 수정 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "수정 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("EDIT","$t")
            },
            retryCall = apiCall,
            context = this
        )
    }

    fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            inputDate // 에러 시 원본 반환
        }
    }
}