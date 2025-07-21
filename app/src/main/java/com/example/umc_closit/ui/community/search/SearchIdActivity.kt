package com.example.umc_closit.ui.community.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

class SearchIdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_id)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_search_result_id)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = SearchIdAdapter(getDummyList())
    }

    // 예시용 더미 데이터
    private fun getDummyList(): List<SearchIdItem> {
        return listOf(
            SearchIdItem("로렌", 288, R.drawable.ic_profile_placeholder),
            SearchIdItem("홍길동", 123, R.drawable.ic_profile_placeholder),
            SearchIdItem("김철수", 456, R.drawable.ic_profile_placeholder),
            SearchIdItem("이영희", 789, R.drawable.ic_profile_placeholder)
        )
    }
}

// 데이터 클래스 예시
data class SearchIdItem(val name: String, val views: Int, val profileResId: Int)