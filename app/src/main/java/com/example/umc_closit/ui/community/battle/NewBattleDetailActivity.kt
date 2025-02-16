// NewBattleDetailActivity.kt
package com.example.umc_closit.ui.community.battle

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.battle.BattlePostRequest
import com.example.umc_closit.data.remote.battle.BattlePostResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.utils.TokenUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewBattleDetailActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var backButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_battle_detail)

        // UI 요소 연결
        titleEditText = findViewById(R.id.et_battle_title)
        uploadButton = findViewById(R.id.btn_upload)
        backButton = findViewById(R.id.iv_back)

        // "뒤로 가기" 버튼 클릭 시
        backButton.setOnClickListener {
            finish()
        }

        // "업로드" 버튼 클릭 시
        uploadButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                uploadBattlePost(title)
            } else {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🟡 API 호출 로직
    private fun uploadBattlePost(title: String) {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = "Bearer ${sharedPreferences.getString("accessToken", "") ?: ""}"

        val request = BattlePostRequest(
            postId = (System.currentTimeMillis() % 100000).toInt(),
            title = title
        )

        val apiCall = {
            RetrofitClient.battleService.uploadBattle(token, request)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response: BattlePostResponse ->
                if (response.isSuccess) {
                    Toast.makeText(
                        this,
                        "업로드 성공! 배틀 ID: ${response.result?.battleId}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "업로드 실패: ${response.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(
                    this,
                    "네트워크 실패: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("UPLOAD_ERROR", "네트워크 실패", t)
            },
            retryCall = apiCall,
            context = this
        )
    }
}
