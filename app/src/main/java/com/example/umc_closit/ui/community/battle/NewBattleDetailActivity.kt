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
import com.bumptech.glide.Glide
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
        val ivImageBig = findViewById<ImageView>(R.id.iv_image_big)

        // ✅ 전달받은 썸네일 URL 가져오기
        val thumbnailUrl = intent.getStringExtra("thumbnail_url")
        val postId = intent.getIntExtra("post_id", -1)


        if (!thumbnailUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(thumbnailUrl)
                .placeholder(R.drawable.img_gray_square)
                .error(R.drawable.img_gray_square)
                .into(ivImageBig)
        } else {
            Toast.makeText(this, "이미지 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }

        // "뒤로 가기" 버튼 클릭 시
        backButton.setOnClickListener {
            finish()
        }

        // "업로드" 버튼 클릭 시
        uploadButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                uploadBattlePost(postId, title)
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
    private fun uploadBattlePost(postId: Int, title: String) {
        val request = BattlePostRequest(
            postId = postId,  // ✅ 전달받은 postId 사용
            title = title
        )

        TokenUtils.handleTokenRefresh(
            call = RetrofitClient.battleApiService.uploadBattle(request),
            onSuccess = { response ->
                if (response.isSuccess) {
                    Toast.makeText(
                        this@NewBattleDetailActivity,
                        "업로드 성공! 배틀 ID: ${response.result?.battleId}",
                        Toast.LENGTH_LONG
                    ).show()

                    // 성공 후 타임라인으로 이동
                    val intent = Intent(this, TimelineActivity::class.java)
                    startActivity(intent)
                    finish()
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
                RetrofitClient.battleApiService.uploadBattle(request)
            },
            context = this@NewBattleDetailActivity
        )
    }

}
