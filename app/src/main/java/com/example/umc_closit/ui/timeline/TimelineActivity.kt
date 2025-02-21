package com.example.umc_closit.ui.timeline

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.umc_closit.R
import com.example.umc_closit.databinding.ActivityTimelineBinding
import com.example.umc_closit.ui.community.CommunityFragment
import com.example.umc_closit.ui.community.todaycloset.TodayClosetFragment
import com.example.umc_closit.ui.mission.MissionActivity
import com.example.umc_closit.ui.profile.ProfileFragment
import com.example.umc_closit.utils.TokenUtils

class TimelineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimelineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 디테일 액티비티에서 넘어온 profileUserClositId를 받는다.
        val profileUserClositId = intent.getStringExtra("profileUserClositId")

        val navigateTo = intent.getStringExtra("navigateTo")

        if (navigateTo == "TodayClosetFragment") {
            replaceFragment(TodayClosetFragment())
        }

        // 기본적으로는 타임라인 화면을 보여주지만,
        // 만약 profileUserClositId가 있으면 해당 프로필 화면을 로드.
        if (profileUserClositId != null) {
            val profileFragment = ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString("profileUserClositId", profileUserClositId)  // 해당 사용자의 clositId를 넘긴다
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, profileFragment)  // fragment_container에 프로필 화면을 로드
                .commit()
        } else {
            // 타임라인 화면 로드
            val fragment = TimelineFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

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
                    val userClositId = TokenUtils.getClositId(this) // 본인 clositId 가져오기
                    val profileFragment = ProfileFragment().apply {
                        arguments = Bundle().apply {
                            putString("profileUserClositId", userClositId) // 본인 clositId 넘기기
                        }
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, profileFragment) // 프로필 프래그먼트로 교체
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
                    val intent = Intent(this, MissionActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // fragment_container는 TimelineActivity의 FrameLayout ID
            .commit()
    }
}
