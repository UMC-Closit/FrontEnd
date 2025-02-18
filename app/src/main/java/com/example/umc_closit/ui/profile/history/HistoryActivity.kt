package com.example.umc_closit.ui.profile.history

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.history.DateHistoryResponse
import com.example.umc_closit.databinding.ActivityHistoryBinding
import com.example.umc_closit.utils.TokenUtils
import java.util.Calendar

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val postThumbnails = mutableMapOf<String, Int>()
    private val postColors = mutableMapOf<String, String>()

    private var currentPage = 0
    private var isLoading = false
    private var hasNextPage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { onBackPressed() }

        setupRecyclerView()
        fetchDateHistoryList()
    }

    private fun fetchDateHistoryList() {
        if (isLoading || !hasNextPage) return

        isLoading = true

        val apiCall = {
            RetrofitClient.historyService.getDateHistoryList(currentPage)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response: DateHistoryResponse ->
                Log.d("HISTORY", "날짜 히스토리 응답: $response")

                response.result?.dateHistoryThumbnailDTOList?.let { result ->
                    result.forEach { item ->
                        val dateKey = item.createdAt.substring(0, 10).replace("/", "-")
                        postThumbnails[dateKey] = item.postId
                        Log.d("HISTORY", "사진 날짜: $dateKey, postId: ${item.postId}")
                    }
                }
                currentPage++
                hasNextPage = response.result?.hasNext ?: false
                isLoading = false
                binding.rvHistory.adapter?.notifyDataSetChanged()
            },
            onFailure = { t ->
                Log.e("HISTORY", "API 실패: ${t.message}")
                isLoading = false
            },
            retryCall = apiCall,
            context = this
        )

    }

    fun fetchPointColorHistoryListForMonth(year: Int, month: Int) {
        val apiCall = {
            RetrofitClient.historyService.getPointColorHistoryList(0)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                Log.d("HISTORY", "포인트 컬러 응답: $response")

                response.result?.colorHistoryThumbnailDTOList?.forEach { item ->
                    val dateKey = item.createdAt.substring(0, 10).replace("/", "-")
                    val monthKey = "$year-${String.format("%02d", month)}"
                    if (dateKey.startsWith(monthKey)) {
                        postColors[dateKey] = item.thumbnail
                        Log.d("HISTORY", "포인트컬러 날짜: $dateKey, 컬러: ${item.thumbnail}")
                    }
                }
                val position = (binding.rvHistory.adapter as? HistoryAdapter)?.months?.indexOf(Pair(year, month))
                position?.let {
                    binding.rvHistory.adapter?.notifyItemChanged(it)
                }
            },
            onFailure = { t ->
                Log.e("HISTORY", "포인트 컬러 API 실패: ${t.message}")
            },
            retryCall = apiCall,
            context = this
        )

    }


    private fun setupRecyclerView() {
        val sharedPool = RecyclerView.RecycledViewPool()

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = HistoryAdapter(
                months = getMonthsFromFirstUpload(Calendar.getInstance()),
                postThumbnails = postThumbnails,
                postColors = postColors,
                historyActivity = this@HistoryActivity,
                sharedPool = sharedPool
            )
        }
    }

    private fun getMonthsFromFirstUpload(firstUploadDate: Calendar): List<Pair<Int, Int>> {
        val months = mutableListOf<Pair<Int, Int>>()
        val currentCalendar = Calendar.getInstance()
        val tempCalendar = firstUploadDate.clone() as Calendar

        while (tempCalendar.before(currentCalendar) ||
            (tempCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                    tempCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH))
        ) {
            val year = tempCalendar.get(Calendar.YEAR)
            val month = tempCalendar.get(Calendar.MONTH) + 1
            months.add(Pair(year, month))
            tempCalendar.add(Calendar.MONTH, 1)
        }
        return months
    }
}
