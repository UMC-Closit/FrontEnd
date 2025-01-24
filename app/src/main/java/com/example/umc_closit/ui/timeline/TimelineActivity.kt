package com.example.umc_closit.ui.timeline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.R
import com.example.umc_closit.databinding.ActivityTimelineBinding

class TimelineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimelineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TimelineFragment 로드
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, TimelineFragment())
            .commit()

        // BottomNavigationView 설정
        binding.btnvTimeline.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_timeline -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, TimelineFragment())
                        .commit()
                    true
                }
/*                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                    true
                }*/
                else -> false
            }
        }
    }
}
