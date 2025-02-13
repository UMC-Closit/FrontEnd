package com.example.umc_closit.ui.timeline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.ui.community.CommunityFragment
import com.example.umc_closit.R
import com.example.umc_closit.databinding.ActivityTimelineBinding
import com.example.umc_closit.ui.profile.ProfileFragment
import com.example.umc_closit.ui.upload.UploadFragment

class TimelineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimelineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ SharedPreferences에서 로그인한 유저 clositId 가져오기
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val clositId = sharedPreferences.getString("clositId", "") ?: ""

        // ✅ "showUploadFragment" 값이 true라면 UploadFragment 표시, 아니면 TimelineFragment 표시
        val fragment = if (intent.getBooleanExtra("showUploadFragment", false)) {
            UploadFragment()
        } else {
            TimelineFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
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
                    val profileFragment = ProfileFragment().apply {
                        arguments = Bundle().apply {
                            putString("profileUserClositId", clositId) // 본인 clositId 넘기기
                        }
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, profileFragment)
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
