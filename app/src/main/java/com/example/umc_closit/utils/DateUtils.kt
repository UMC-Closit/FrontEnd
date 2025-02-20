package com.example.umc_closit.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    fun parseUploadDate(uploadDate: String): String {
        return try {
            val date = inputFormat.parse(uploadDate)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun parseUploadDateToDate(uploadDate: String): Date {
        return try {
            inputFormat.parse(uploadDate) ?: Date()
        } catch (e: Exception) {
            e.printStackTrace()
            Date()
        }
    }


    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yy.MM.dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // 특정 연도와 월의 총 일 수 반환
    fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // 📌 YYYY-MM-DD 형식의 날짜를 받아서 연/월을 반환하는 함수
    fun getYearMonthFromDate(dateString: String): Pair<Int, Int> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return Pair(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
    }

    // 특정 연도와 월의 첫 번째 날의 요일 반환 (0 = 일요일, 1 = 월요일, ..., 6 = 토요일)
    fun getFirstDayOfWeek(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1) // 1일로 설정
        return calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0부터 시작하도록 조정
    }

    // 현재 연도와 월을 "YYYY년 MM월" 형식으로 반환
    fun getMonthYearString(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("yyyy년 MM월", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    // 현재 시간을 "yyyy-MM-dd'T'HH:mm:ss.SSSSSS" 형식으로 반환
    fun getFormattedTime(currentTimeMillis: Long): String {
        val currentDate = Date(currentTimeMillis)
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()).format(currentDate)
    }


    // 주어진 날짜를 기반으로 "n초 전", "n분 전", "n시간 전" 등으로 반환
    private val inputFormatDash =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()) // 2025-02-19T06:45:10.200Z
    private val inputFormatSlash =
        SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()) // 2025/02/19 16:51:44

    fun getTimeAgo(dateString: String): String {
        if (dateString == "Just now") {
            return dateString
        }

        val date: Date? = try {
            inputFormatDash.parse(dateString)
        } catch (e: Exception) {
            try {
                inputFormatSlash.parse(dateString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        if (date == null) return "날짜 오류"

        val now = Date()
        val diffInMillis = now.time - date.time
        val diffInSeconds = diffInMillis / 1000
        val diffInMinutes = diffInMillis / (60 * 1000)
        val diffInHours = diffInMillis / (60 * 60 * 1000)
        val diffInDays = diffInMillis / (24 * 60 * 60 * 1000)

        return when {
            diffInSeconds < 10 -> "Just now"
            diffInSeconds < 60 -> "${diffInSeconds}초 전"
            diffInMinutes < 60 -> "${diffInMinutes}분 전"
            diffInHours < 24 -> "${diffInHours}시간 전"
            diffInDays < 30 -> "${diffInDays}일 전"
            else -> "몇 달 전"
        }
    }

}