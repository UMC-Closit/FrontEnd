package com.example.umc_closit.ui.profile.history

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.data.TimelineItem
import com.example.umc_closit.databinding.ActivityHistoryBinding
import com.example.umc_closit.model.TimelineViewModel
import com.example.umc_closit.utils.DateUtils
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val timelineViewModel: TimelineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼 설정
        binding.ivBack.setOnClickListener { onBackPressed() }

        // 첫 게시글 업로드 날짜 가져오기 (가장 오래된 데이터 기준)
        timelineViewModel.timelineItems.observe(this) { timelineItems ->
            val firstUploadDate = getFirstUploadDate(timelineItems)
            getMonthsFromFirstUpload(firstUploadDate)

            // postThumbnails 매핑
            mapTimelineItemsToCalendar(timelineItems)

            setupRecyclerView()
        }
    }

    private fun getFirstUploadDate(items: List<TimelineItem>): Calendar {
        val firstUploadDate = items.minByOrNull { it.uploadDate }?.uploadDate ?: return Calendar.getInstance()
        return Calendar.getInstance().apply {
            time = DateUtils.parseUploadDateToDate(firstUploadDate)
        }
    }


    private fun mapTimelineItemsToCalendar(timelineItems: List<TimelineItem>): Map<String, Int> {
        val postThumbnails = mutableMapOf<String, Int>() // ✅ 날짜 → TimelineItem ID 매핑

        for (item in timelineItems) {
            val formattedDate = DateUtils.parseUploadDate(item.uploadDate) // "yyyy-MM-dd" 변환
            if (!postThumbnails.containsKey(formattedDate)) {
                postThumbnails[formattedDate] = item.id // ✅ 날짜 → ID 저장
            }
        }
        return postThumbnails
    }




    private fun setupRecyclerView() {
        val sharedPool = RecyclerView.RecycledViewPool()

        timelineViewModel.timelineItems.observe(this) { timelineItems ->
            val mappedPostThumbnails = mapTimelineItemsToCalendar(timelineItems) // ✅ 여기에서 미리 변환

            binding.rvHistory.apply {
                layoutManager = LinearLayoutManager(this@HistoryActivity)
                adapter = HistoryAdapter(
                    months = getMonthsFromFirstUpload(getFirstUploadDate(timelineItems)), // ✅ 월 리스트 전달
                    postThumbnails = mappedPostThumbnails, // ✅ 변환된 데이터 전달
                    viewModelStoreOwner = this@HistoryActivity, // ✅ ViewModelStoreOwner 전달
                    sharedPool = RecyclerView.RecycledViewPool()
                )
            }
        }
    }

    companion object {
        fun getMonthsFromFirstUpload(firstUploadDate: Calendar): List<Pair<Int, Int>> {
            val months = mutableListOf<Pair<Int, Int>>() // ✅ 연도와 월을 Pair로 저장
            val currentCalendar = Calendar.getInstance()

            val tempCalendar = firstUploadDate.clone() as Calendar

            while (tempCalendar.before(currentCalendar) ||
                (tempCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                        tempCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH))) {

                val year = tempCalendar.get(Calendar.YEAR)
                val month = tempCalendar.get(Calendar.MONTH) + 1 // 1월 ~ 12월

                months.add(Pair(year, month)) // ✅ 연도와 월을 저장

                tempCalendar.add(Calendar.MONTH, 1) // ✅ 다음 달로 이동
            }

            return months
        }
    }

}


