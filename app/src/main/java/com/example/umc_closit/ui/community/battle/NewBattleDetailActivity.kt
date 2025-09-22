// NewBattleDetailActivity.kt
package com.example.umc_closit.ui.community.battle

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.battle.BattleDetailResult
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.utils.TokenUtils
import com.google.android.material.progressindicator.LinearProgressIndicator

class NewBattleDetailActivity : AppCompatActivity() {

    private lateinit var uploadButton: Button
    private lateinit var backButton: ImageView
    
    // UI 요소들
    private lateinit var tvDate: TextView
    private lateinit var tvInfoTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var ivLeftBig: ImageView
    private lateinit var ivLeftSmall: ImageView
    private lateinit var ivRight: ImageView
    private lateinit var tvUsernameLeft: TextView
    private lateinit var tvUsernameRight: TextView

    private var battleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_challenge_detail)

        // Intent에서 battleId 가져오기
        battleId = intent.getIntExtra("battle_id", -1)
        
        if (battleId == -1) {
            Toast.makeText(this, "배틀 ID가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupClickListeners()
        
        // API 호출하여 배틀 상세 정보 가져오기
        loadBattleDetail()
    }
    
    private fun initViews() {
        // 툴바
        backButton = findViewById(R.id.iv_back)
        
        // 정보 패널
        tvDate = findViewById(R.id.tv_date)
        tvInfoTitle = findViewById(R.id.tv_info_title)
        tvDescription = findViewById(R.id.tv_description)
        uploadButton = findViewById(R.id.btnUpload)
        
        // 왼쪽 카드
        ivLeftBig = findViewById(R.id.iv_left_big)
        ivLeftSmall = findViewById(R.id.iv_left_small)
        tvUsernameLeft = findViewById(R.id.tv_username_left)
        
        // 오른쪽 카드
        ivRight = findViewById(R.id.iv_right)
        tvUsernameRight = findViewById(R.id.tv_username_right)
    }
    
    private fun setupClickListeners() {
        // "뒤로 가기" 버튼 클릭 시
        backButton.setOnClickListener {
            finish()
        }

        // "업로드" 버튼 클릭 시
        uploadButton.setOnClickListener {
            // 도전하기 기능 (필요시 구현)
            Toast.makeText(this, "도전하기 기능은 추후 구현 예정입니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadBattleDetail() {
        TokenUtils.handleTokenRefresh(
            call = RetrofitClient.battleApiService.getBattleDetail(battleId),
            onSuccess = { response ->
                if (response.isSuccess) {
                    bindBattleData(response.result)
                } else {
                    Toast.makeText(this, "배틀 정보를 불러오는데 실패했습니다: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { throwable ->
                Log.e("BattleDetail", "API 호출 실패", throwable)
                Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            },
            context = this
        )
    }
    
    private fun bindBattleData(battleData: BattleDetailResult) {
        // 기본 정보
        tvInfoTitle.text = battleData.title
        tvDescription.text = battleData.description
        
        // 날짜 포맷팅 (ISO 8601 형식을 한국어 형식으로 변환)
        val formattedDate = formatDate(battleData.deadline)
        tvDate.text = formattedDate
        
        // 왼쪽 카드 (첫 번째 참가자)
        Glide.with(this)
            .load(battleData.firstPostFrontImage)
            .placeholder(R.drawable.img_detail_big_default)
            .error(R.drawable.img_detail_big_default)
            .into(ivLeftBig)
            
        Glide.with(this)
            .load(battleData.firstPostBackImage)
            .placeholder(R.drawable.img_detail_small_default)
            .error(R.drawable.img_detail_small_default)
            .into(ivLeftSmall)
            
        tvUsernameLeft.text = battleData.firstClositId
        
        // 오른쪽 카드 (두 번째 참가자)
        Glide.with(this)
            .load(battleData.secondPostFrontImage)
            .placeholder(R.drawable.image_background)
            .error(R.drawable.image_background)
            .into(ivRight)
            
        tvUsernameRight.text = battleData.secondClositId
        
        // 투표 수 계산 및 프로그레스 바 업데이트
        updateProgressBars(battleData.firstVotingCnt, battleData.secondVotingCnt)
    }
    
    private fun updateProgressBars(firstVotes: Int, secondVotes: Int) {
        val totalVotes = firstVotes + secondVotes
        
        if (totalVotes > 0) {
            val firstPercentage = (firstVotes.toFloat() / totalVotes * 100).toInt()
            val secondPercentage = (secondVotes.toFloat() / totalVotes * 100).toInt()
            
            // 프로그레스 바가 있다면 업데이트
            try {
                val progressLeft = findViewById<LinearProgressIndicator>(R.id.progress_left)
                val progressRight = findViewById<LinearProgressIndicator>(R.id.progress_right)
                
                progressLeft?.progress = firstPercentage
                progressRight?.progress = secondPercentage
            } catch (e: Exception) {
                Log.d("ProgressBar", "프로그레스 바를 찾을 수 없습니다: ${e.message}")
            }
        }
    }
    
    private fun formatDate(isoDate: String): String {
        return try {
            // ISO 8601 형식 (2025-09-22T05:31:03.466Z)을 한국어 형식으로 변환
            val datePart = isoDate.substring(0, 10) // 2025-09-22
            val parts = datePart.split("-")
            if (parts.size == 3) {
                val month = parts[1].toInt()
                val day = parts[2].toInt()
                "${month}월 ${day}일"
            } else {
                isoDate
            }
        } catch (e: Exception) {
            Log.e("DateFormat", "날짜 포맷팅 실패", e)
            isoDate
        }
    }

}
