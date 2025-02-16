// NewBattleDetailActivity.kt
package com.example.umc_closit.ui.community.battle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R
import com.example.umc_closit.data.BattlePostRequest
import com.example.umc_closit.data.BattlePostResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.ui.timeline.TimelineActivity
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
                val intent = Intent(this, TimelineActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 배틀 업로드 API 호출
     */
    private fun uploadBattlePost(title: String) {
        val authToken = "Bearer ${TokenUtils.getAccessToken(this)}"

        val request = BattlePostRequest(
            postId = (System.currentTimeMillis() % 100000).toInt(), // 간단한 임시 ID
            title = title
        )

        TokenUtils.handleTokenRefresh(
            call = RetrofitClient.battleApiService.uploadBattle(authToken, request),
            onSuccess = { response ->
                if (response.isSuccess) {
                    Toast.makeText(
                        this@NewBattleDetailActivity,
                        "업로드 성공! 배틀 ID: ${response.result?.battleId}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@NewBattleDetailActivity,
                        "업로드 실패: ${response.message ?: "알 수 없는 오류"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            onFailure = { throwable ->
                Log.e("BattleUpload", "API 호출 실패", throwable)
                Toast.makeText(this@NewBattleDetailActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
            },
            retryCall = {
                val newAuthToken = "Bearer ${TokenUtils.getAccessToken(this)}"
                RetrofitClient.battleApiService.uploadBattle(newAuthToken, request)
            },
            context = this@NewBattleDetailActivity
        )
    }

}
