package com.example.umc_closit.ui.timeline

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.databinding.ActivityTimelineBinding
import com.example.umc_closit.model.TimelineViewModel
import com.example.umc_closit.data.TimelineItem
import com.example.umc_closit.ui.timeline.notification.NotificationActivity

class TimelineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimelineBinding
    private val timelineViewModel: TimelineViewModel by viewModels()  // ViewModel 연결
    private lateinit var timelineAdapter: TimelineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Notification 아이콘 클릭 시 NotificationActivity로 이동
        binding.ivNotification.setOnClickListener {
            // NotificationActivity로 이동하는 Intent 생성
            val intent = Intent(this, NotificationActivity::class.java)


            startActivity(intent)
        }

        // RecyclerView 설정
        timelineAdapter = TimelineAdapter(this, mutableListOf(), mutableListOf()) // 초기 데이터는 빈 리스트로 설정
        binding.rvTimeline.apply {
            layoutManager = LinearLayoutManager(this@TimelineActivity)
            adapter = timelineAdapter  // RecyclerView에 어댑터 설정
        }

        // LiveData 관찰: timelineItems의 변경이 있을 때마다 RecyclerView 갱신
        timelineViewModel.timelineItems.observe(this@TimelineActivity, Observer { timelineItems ->
            timelineAdapter.updateTimelineItems(timelineItems)  // 어댑터 업데이트
        })

        // 예시로 1번 아이템에 대해 좋아요 토글
        timelineViewModel.toggleLike(1)
    }
}