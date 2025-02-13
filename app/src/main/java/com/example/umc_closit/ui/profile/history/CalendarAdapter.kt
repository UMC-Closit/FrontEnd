package com.example.umc_closit.ui.profile.history

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R
import com.example.umc_closit.data.entities.TimelineItem
import com.example.umc_closit.data.remote.PostPreview
import com.example.umc_closit.databinding.ItemCalendarDayBinding
import com.example.umc_closit.ui.profile.highlight.AddHighlightActivity
import com.example.umc_closit.model.TimelineViewModel
import java.util.Calendar

class CalendarAdapter(
    private val year: Int,
    private val month: Int,
    private val postThumbnails: Map<String, Int>,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val weekdayWidth: Int,
    private val isMonthSelected: Boolean // ✅ 선택된 월인지 여부 전달
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val timelineViewModel: TimelineViewModel by lazy {
        ViewModelProvider(viewModelStoreOwner).get(TimelineViewModel::class.java)
    }

    class CalendarViewHolder(val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val days: List<String>
    private val currentYear: Int = year
    private val currentMonth: Int = month

    init {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysInMonth = (1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).map { it.toString() }

        val emptyDays = List(firstDayOfWeek - 1) { "" }
        days = emptyDays + daysInMonth
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = days[position]

        with(holder.binding) {
            if (day.isEmpty()) {
                tvDay.text = ""
                ivCalendar.setImageResource(android.R.color.transparent)
                return
            }

            tvDay.text = day
            val fullDateKey = "$currentYear-${String.format("%02d", currentMonth)}-${String.format("%02d", day.toInt())}"
            val timelineItemId = postThumbnails[fullDateKey]

/*
            val timelineItem: PostPreview? = timelineViewModel.timelineItems.value?.find { it.id == timelineItemId }
            val mainImageResId = PostPreview?.mainImageResId ?: R.drawable.img_history_calendar_default
            val pointColor = timelineItem?.pointColor ?: "#D9D9D9"

            ivCalendar.setImageResource(mainImageResId)

            // ✅ 해당 월이 선택되었을 때만 `pointColor` 적용
            if (isMonthSelected) {
                ivCalendar.setColorFilter(android.graphics.Color.parseColor(pointColor))
            } else {
                ivCalendar.clearColorFilter()
            }
*/

            // ✅ 크기 조정 유지
            val calculatedWidth = weekdayWidth
            val calculatedHeight = (calculatedWidth * 5) / 3
            ivCalendar.layoutParams = ivCalendar.layoutParams.apply {
                width = calculatedWidth
                height = calculatedHeight
            }

            // ✅ 클릭 이벤트 추가
            root.setOnClickListener {
                timelineItemId?.let { id ->
                    val context = holder.itemView.context
                    val intent = Intent(context, AddHighlightActivity::class.java).apply {
                        putExtra("timeline_item_id", id)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = days.size
}
