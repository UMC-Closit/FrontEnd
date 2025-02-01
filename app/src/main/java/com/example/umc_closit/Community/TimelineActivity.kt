package com.example.umc_closit.Community

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R
import com.example.umc_closit.Community.CommunityFragment
import com.example.umc_closit.databinding.ActivityTimelineBinding

class TimelineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimelineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 Fragment 설정 (첫 실행 시 CommunityFragment 표시)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommunityFragment())
                .commit()
        }

        // BottomNavigationView 설정 (Fragment 교체)
        binding.btnvTimeline.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_community -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CommunityFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
