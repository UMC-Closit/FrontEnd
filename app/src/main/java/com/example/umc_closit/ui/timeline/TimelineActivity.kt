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

        val profileUserClositId = intent.getStringExtra("profileUserClositId")
        val navigateTo = intent.getStringExtra("navigateTo")

        // 1️⃣ `navigateTo` 값에 따라 초기 프래그먼트 설정
        val initialFragment: Fragment = when (navigateTo) {
            "TodayClosetFragment" -> {
                binding.btnvTimeline.selectedItemId = R.id.menu_community // 👈 여기서 아이콘 활성화 변경
                TodayClosetFragment()
            }
            else -> if (profileUserClositId != null) {
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString("profileUserClositId", profileUserClositId)
                    }
                }
            } else {
                TimelineFragment()
            }
        }

        replaceFragment(initialFragment) // 초기 프래그먼트 설정

        // 2️⃣ BottomNavigationView 설정 (항상 동작하도록)
        binding.btnvTimeline.setOnItemSelectedListener { item ->
            android.util.Log.d("TimelineActivity", "BottomNavigationView 클릭: ${item.title}")
            when (item.itemId) {
                R.id.menu_timeline -> {
                    android.util.Log.d("TimelineActivity", "TimelineFragment로 전환")
                    replaceFragment(TimelineFragment())
                    true
                }
                R.id.menu_profile -> {
                    android.util.Log.d("TimelineActivity", "ProfileFragment로 전환")
                    val userClositId = TokenUtils.getClositId(this)
                    val profileFragment = ProfileFragment().apply {
                        arguments = Bundle().apply {
                            putString("profileUserClositId", userClositId)
                        }
                    }
                    replaceFragment(profileFragment)
                    true
                }
                R.id.menu_community -> {
                    android.util.Log.d("TimelineActivity", "CommunityFragment로 전환")
                    val communityFragment = CommunityFragment()
                    android.util.Log.d("TimelineActivity", "CommunityFragment 인스턴스 생성됨")
                    replaceFragment(communityFragment)
                    true
                }
                R.id.menu_upload -> {
                    android.util.Log.d("TimelineActivity", "MissionActivity로 전환")
                    val intent = Intent(this, MissionActivity::class.java)
                    startActivity(intent)
                    finish() // TimelineActivity 종료
                    true
                }
                else -> {
                    android.util.Log.d("TimelineActivity", "알 수 없는 메뉴 아이템: ${item.itemId}")
                    false
                }
            }
        }
    }



    private fun replaceFragment(fragment: Fragment) {
        android.util.Log.d("TimelineActivity", "프래그먼트 전환: ${fragment.javaClass.simpleName}")
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragment_container, fragment) // fragment_container는 TimelineActivity의 FrameLayout ID
            .commit()
    }
}
