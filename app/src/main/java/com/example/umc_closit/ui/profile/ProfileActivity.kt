package com.example.umc_closit.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R
import com.example.umc_closit.data.HighlightItem
import com.example.umc_closit.ui.profile.highlight.HighlightAdapter

class ProfileActivity : AppCompatActivity() {

    // highlightAdapter를 클래스 멤버 변수로 선언
    private lateinit var highlightAdapter: HighlightAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // 레이아웃 파일 설정

        // 샘플 데이터
        val highlightItems = mutableListOf(
            HighlightItem(R.drawable.img_profile_highlight, "24.12.07"),
            HighlightItem(R.drawable.img_profile_highlight, "24.12.08"),
            HighlightItem(R.drawable.img_profile_highlight, "24.12.09")
        )

        // highlightAdapter 초기화
        highlightAdapter = HighlightAdapter(highlightItems) {
            // "+" 버튼 클릭 시 동작 (예: 새로운 하이라이트 추가)
            highlightItems.add(
                HighlightItem(R.drawable.img_profile_add_highlight, "24.12.10")
            )
            highlightAdapter.updateItems(highlightItems) // 어댑터 업데이트
        }

        // 리사이클러뷰 설정
        val highlightsRecyclerView: RecyclerView = findViewById(R.id.highlightsRecyclerView)
        highlightsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        highlightsRecyclerView.adapter = highlightAdapter
    }
}
