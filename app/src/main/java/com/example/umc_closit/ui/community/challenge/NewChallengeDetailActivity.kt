package com.example.umc_closit.ui.community.challenge

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.battle.BattleChallengeRequest
import com.example.umc_closit.data.remote.battle.BattleChallengeResponse
import com.example.umc_closit.data.remote.battle.ChallengeBattlePreview
import com.example.umc_closit.utils.TokenUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NewChallengeDetailActivity : AppCompatActivity() {

    private lateinit var ivImage1Big: ImageView
    private lateinit var ivImage2Big: ImageView
    private lateinit var btnUpload: ImageButton

    private lateinit var challengeData: ChallengeBattlePreview
    private var thumbnailUrl: String? = null
    private var postId: Int = -1
    private var myClositId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_challenge_detail)

        // View 초기화
        ivImage1Big = findViewById(R.id.iv_image1_big)
        ivImage2Big = findViewById(R.id.iv_image2_big)
        btnUpload = findViewById(R.id.btnUpload)

        // Intent로부터 데이터 수신
        thumbnailUrl = intent.getStringExtra("thumbnail_url")
        postId = intent.getIntExtra("post_id", -1)
        challengeData = intent.getParcelableExtra("challenge_data") ?: return

        // 현재 사용자의 ClositId 가져오기
        myClositId = TokenUtils.getClositId(this) ?: ""

        // 이미지 로드
        loadImages()

        // 업로드 버튼 클릭 이벤트
        btnUpload.setOnClickListener {
            if (postId != -1 && thumbnailUrl != null) {
                uploadChallenge()
            } else {
                Toast.makeText(this, "업로드할 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 이미지 로드
     */
    private fun loadImages() {
        // 첫 번째 이미지: challengeData.firstPostFrontImage
        Glide.with(this)
            .load(challengeData.firstPostFrontImage)
            .placeholder(R.drawable.img_detail_big_default)
            .error(R.drawable.img_detail_big_default)
            .into(ivImage1Big)

        // 두 번째 이미지: thumbnailUrl
        Glide.with(this)
            .load(thumbnailUrl)
            .placeholder(R.drawable.img_detail_big_default)
            .error(R.drawable.img_detail_big_default)
            .into(ivImage2Big)
    }

    /**
     * 배틀 도전 API 호출
     */
    private fun uploadChallenge() {
        // 현재 시간 포맷
        val currentTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            .format(Date())

        // BattleChallengeRequest 생성
        val request = BattleChallengeRequest(
            postId = postId
        )

        // API 호출
        RetrofitClient.battleApiService.challengeBattle(challengeData.battleId, request)
            .enqueue(object : Callback<BattleChallengeResponse> {
                override fun onResponse(
                    call: Call<BattleChallengeResponse>,
                    response: Response<BattleChallengeResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            Toast.makeText(
                                this@NewChallengeDetailActivity,
                                "배틀 도전 성공!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish() // 성공 시 종료
                        } else {
                            Toast.makeText(
                                this@NewChallengeDetailActivity,
                                "도전 실패: ${result?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e("API_ERROR", "응답 실패: ${response.code()} - ${response.message()}")
                        Toast.makeText(
                            this@NewChallengeDetailActivity,
                            "도전 실패: 서버 에러",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BattleChallengeResponse>, t: Throwable) {
                    Log.e("API_ERROR", "네트워크 오류: ${t.message}")
                    Toast.makeText(
                        this@NewChallengeDetailActivity,
                        "네트워크 오류가 발생했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
