package com.example.umc_closit.ui.timeline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.replace
import com.example.umc_closit.Community.CommunityFragment
import com.example.umc_closit.R
import com.example.umc_closit.databinding.ActivityTimelineBinding
import com.example.umc_closit.ui.profile.ProfileFragment
import com.example.umc_closit.ui.upload.UploadAdapter
import com.example.umc_closit.ui.upload.UploadFragment

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

               R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                    true
                }
                R.id.menu_community -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CommunityFragment())
                        .commit()
                    true
                }
                R.id.menu_upload -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, UploadFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
