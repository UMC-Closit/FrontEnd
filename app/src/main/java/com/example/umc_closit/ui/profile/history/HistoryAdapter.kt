package com.example.umc_closit.ui.profile.history

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.databinding.ItemCalendarMonthBinding

class HistoryAdapter(
    val months: List<Pair<Int, Int>>,
    private val postThumbnails: Map<String, Int>,
    private val postColors: MutableMap<String, String>,
    private val historyActivity: HistoryActivity,
    private val sharedPool: RecyclerView.RecycledViewPool
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val selectedMonths = mutableSetOf<Pair<Int, Int>>()


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

        Log.d("HISTORY", "onBindViewHolder: ${year}년 ${month}월, 선택 여부: $isSelected")

        with(holder.binding) {
            tvMonth.text = "${year}년 ${month}월"

            viewColorCircle.setOnClickListener {
                if (isSelected) {
                    selectedMonths.remove(Pair(year, month))
                    postColors.keys.filter { it.startsWith(currentMonthKey) }
                        .forEach { postColors.remove(it) }
                    Log.d("HISTORY", "$currentMonthKey -> 해제됨, postColors에서 제거")
                } else {
                    selectedMonths.add(Pair(year, month))
                    historyActivity.fetchPointColorHistoryListForMonth(year, month)
                    Log.d("HISTORY", "$currentMonthKey -> 선택됨, 포인트 컬러 요청")
                }
                notifyItemChanged(position)
            }

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(if (isSelected) Color.parseColor("#BDBDBD") else Color.WHITE)
                setStroke(2, Color.BLACK)
            }
            viewColorCircle.background = drawable

            rvCalendar.post {
                val scale = root.context.resources.displayMetrics.density
                val parentWidth = tvSun.width - (8 * scale).toInt()

                val calendarAdapter = CalendarAdapter(
                    year = year,
                    month = month,
                    postThumbnails = postThumbnails,
                    postColors = postColors,
                    weekdayWidth = parentWidth.toInt()
                )

                rvCalendar.adapter = calendarAdapter
                rvCalendar.layoutManager = GridLayoutManager(holder.binding.root.context, 7)
                rvCalendar.setRecycledViewPool(sharedPool)
                calendarAdapter.notifyDataSetChanged() // 새로고침 보장
            }

        }
    }

    override fun getItemCount(): Int = months.size
}
