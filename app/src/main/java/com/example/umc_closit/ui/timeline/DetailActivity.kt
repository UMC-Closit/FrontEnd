package com.example.umc_closit.ui.timeline

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.umc_closit.R
import com.example.umc_closit.data.TimelineItem
import com.example.umc_closit.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val hashtags = listOf("#Nature", "#Sunset", "#Travel", "#Adventure") // 예시 해시태그 목록

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Flow 레이아웃 및 부모 ConstraintLayout 가져오기
        val flow: Flow = binding.flowHashtagContainer
        val parentLayout: ConstraintLayout = binding.clHashtag

        // 해시태그 불러오기
        loadHashtags(flow, parentLayout)

        // Intent로 전달된 TimelineItem 받기
        val timelineItem = intent.getParcelableExtra<TimelineItem>("timelineItem")

        // 데이터를 바인딩
        timelineItem?.let {
            binding.ivImageBig.setImageResource(it.mainImageResId) // 큰 이미지
            binding.ivImageSmall.setImageResource(it.overlayImageResId) // 겹쳐진 이미지
            Glide.with(this)
                .load(it.userProfileResId) // 유저 프로필 이미지
                .apply(RequestOptions.circleCropTransform()) // 둥글게 자르기
                .into(binding.ivUserProfile)
        }

        // 뒤로가기 버튼 클릭 리스너 설정
        binding.ivBack.setOnClickListener {
            onBackPressed()  // 뒤로 가기
        }
    }

    // 해시태그 불러오기
    private fun loadHashtags(flow: Flow, parentLayout: ConstraintLayout) {
        for (hashtag in hashtags) {
            createHashtagTextView(hashtag, parentLayout, flow)
        }
    }

    // 해시태그 TextView 생성
    private fun createHashtagTextView(text: String, parentLayout: ConstraintLayout, flow: Flow) {
        val textView = TextView(this).apply {
            id = View.generateViewId()
            this.text = text
            textSize = 18f
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setBackgroundResource(R.drawable.bg_detail_hashtag) // 해시태그 배경
            setPadding(30, 8, 30, 8)

            setOnClickListener {
                Toast.makeText(context, "해시태그: $text", Toast.LENGTH_SHORT).show()
            }
        }

        parentLayout.addView(textView)
        flow.addView(textView)
    }
}