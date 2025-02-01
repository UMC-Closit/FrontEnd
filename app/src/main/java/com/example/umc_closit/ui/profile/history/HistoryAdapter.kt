package com.example.umc_closit.ui.profile.history

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R
import com.example.umc_closit.databinding.ItemCalendarMonthBinding

class HistoryAdapter(
    private val months: List<Pair<Int, Int>>,
    private val postThumbnails: Map<String, Int>,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val sharedPool: RecyclerView.RecycledViewPool
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val selectedMonths = mutableSetOf<Pair<Int, Int>>() // ✅ 선택된 월 저장

    class HistoryViewHolder(val binding: ItemCalendarMonthBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemCalendarMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val (year, month) = months[position]
        val isSelected = selectedMonths.contains(Pair(year, month))

        val currentMonthKey = "$year-${String.format("%02d", month)}"
        val filteredThumbnails = postThumbnails.filterKeys { key -> key.startsWith(currentMonthKey) }

        with(holder.binding) {
            tvMonth.text = "${year}년 ${month}월"

            rvCalendar.post {
                val scale = root.context.resources.displayMetrics.density
                val parentWidth = tvSun.width - (8 * scale).toInt()

                val calendarAdapter = CalendarAdapter(
                    year = year,
                    month = month,
                    postThumbnails = filteredThumbnails,
                    viewModelStoreOwner = viewModelStoreOwner,
                    weekdayWidth = parentWidth,
                    isMonthSelected = isSelected
                )

                rvCalendar.apply {
                    layoutManager = GridLayoutManager(context, 7)
                    adapter = calendarAdapter
                    setRecycledViewPool(sharedPool)
                }
            }

            // ✅ viewColorCircle 클릭 시 해당 월 선택 상태 변경
            viewColorCircle.setOnClickListener {
                if (isSelected) {
                    selectedMonths.remove(Pair(year, month))
                } else {
                    selectedMonths.add(Pair(year, month))
                }
                notifyItemChanged(position)
            }

            // ✅ 선택 상태 확인 후 배경 변경
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(if (isSelected) Color.parseColor("#BDBDBD") else Color.WHITE)
                setStroke(2, Color.BLACK)
            }
            viewColorCircle.background = drawable
        }
    }

    override fun getItemCount(): Int = months.size
}
