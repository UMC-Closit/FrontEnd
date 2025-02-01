package com.example.umc_closit.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    // 현재 시간을 "yyyy-MM-dd'T'HH:mm:ss.SSSSSS" 형식으로 반환
    fun getFormattedTime(currentTimeMillis: Long): String {
        val currentDate = Date(currentTimeMillis)
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()).format(currentDate)
    }

    // 주어진 날짜를 기반으로 "n초 전", "n분 전", "n시간 전" 등으로 반환
    fun getTimeAgo(dateString: String): String {
        if (dateString == "Just now") {
            return dateString
        }

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        val now = Date()

        val diffInMillis = now.time - date.time // 현재 시간 - 주어진 시간
        val diffInSeconds = diffInMillis / 1000
        val diffInMinutes = diffInMillis / (60 * 1000)
        val diffInHours = diffInMillis / (60 * 60 * 1000)
        val diffInDays = diffInMillis / (24 * 60 * 60 * 1000)

        return when {
            // 10초 이내
            diffInSeconds < 10 -> "Just now"
            // 1분 이내
            diffInSeconds < 60 -> "${diffInSeconds}초 전"
            // 1시간 이내
            diffInMinutes < 60 -> "${diffInMinutes}분 전"
            // 하루 이내
            diffInHours < 24 -> "${diffInHours}시간 전"
            // 한 달 이내
            diffInDays < 30 -> "${diffInDays}일 전"
            // 그 외
            else -> "몇 달 전"
        }
    }
}