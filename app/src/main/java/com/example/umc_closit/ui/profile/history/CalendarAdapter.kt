package com.example.umc_closit.ui.profile.history

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.databinding.ItemCalendarDayBinding
import com.example.umc_closit.ui.profile.highlight.AddHighlightActivity
import com.example.umc_closit.utils.TokenUtils
import java.util.Calendar

class CalendarAdapter(
    private val year: Int,
    private val month: Int,
    private val postThumbnails: MutableMap<String, String>,
    private val postColors: Map<String, String>,
    private val weekdayWidth: Int
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

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
                ivCalendar.clearColorFilter()
                return
            }

            tvDay.text = day
            val fullDateKey =
                "$currentYear-${String.format("%02d", currentMonth)}-${String.format("%02d", day.toInt())}"

            // URL이 들어가도록 변경
            val thumbnailUrl = postThumbnails[fullDateKey]

            // 색상 파싱 안전하게 처리
            val pointColorHex = postColors[fullDateKey]?.let { color ->
                when {
                    color.matches(Regex("^#[0-9A-Fa-f]{6}$")) -> color // #RRGGBB
                    color.matches(Regex("^#[0-9A-Fa-f]{8}$")) -> color // #AARRGGBB
                    color.matches(Regex("^[0-9A-Fa-f]{6}$")) -> "#$color" // RRGGBB
                    color.matches(Regex("^[0-9A-Fa-f]{8}$")) -> "#$color" // AARRGGBB
                    else -> {
                        Log.e("HISTORY", "잘못된 색상 코드: $color -> null 처리")
                        "#D9D9D9"
                    }
                }
            }


            Log.d("HISTORY", "onBindViewHolder 날짜: $fullDateKey, 포스트 ID: $thumbnailUrl, 색상: $pointColorHex")

            // 사진 설정 (사진이 있든 없든 확인용 임시 이미지 넣기)
            if (thumbnailUrl != null) {
                Glide.with(root.context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.img_history_calendar_default)
                    .error(R.drawable.img_history_calendar_default)
                    .into(ivCalendar)
            } else {
                ivCalendar.setImageResource(R.drawable.img_history_calendar_default)
            }

            // 색상 설정 (안전하게)
            if (!pointColorHex.isNullOrEmpty()) {
                try {
                    ivCalendar.setColorFilter(Color.parseColor(pointColorHex))
                } catch (e: IllegalArgumentException) {
                    ivCalendar.clearColorFilter() // 파싱 실패시 기본상태로
                    Log.e("HISTORY", "색상 파싱 실패: $pointColorHex")
                }
            } else {
                ivCalendar.clearColorFilter()
            }


            // 크기 조정 유지
            val calculatedWidth = weekdayWidth
            val calculatedHeight = (calculatedWidth * 5) / 3
            ivCalendar.layoutParams = ivCalendar.layoutParams.apply {
                width = calculatedWidth
                height = calculatedHeight
            }

            // 클릭 이벤트 추가
            root.setOnClickListener {
                if (day.isEmpty()) return@setOnClickListener

                val context = holder.itemView.context
                val dateKey = "$currentYear-${String.format("%02d", currentMonth)}-${String.format("%02d", day.toInt())}"

                val apiCall = {
                    RetrofitClient.historyService.getDetailHistory(dateKey)
                }

                TokenUtils.handleTokenRefresh(
                    call = apiCall(),
                    onSuccess = { response ->
                        if (response.isSuccess) {
                            val postIdList = response.result.postList.map { it.postId }
                            if (postIdList.isNotEmpty()) {
                                val intent = Intent(context, AddHighlightActivity::class.java).apply {
                                    putIntegerArrayListExtra("postIdList", ArrayList(postIdList))
                                    putExtra("currentPosition", 0) // 처음엔 첫 번째 게시글
                                }
                                context.startActivity(intent)
                            } else {
                                Log.d("CALENDAR", "해당 날짜에 게시물이 없음")
                            }
                        } else {
                            Log.e("CALENDAR", "히스토리 상세 조회 실패: ${response.message}")
                        }
                    },
                    onFailure = { t ->
                        Log.e("CALENDAR", "네트워크 오류: ${t.message}")
                    },
                    retryCall = apiCall,
                    context = context
                )
            }

        }
    }

    override fun getItemCount(): Int = days.size
}
