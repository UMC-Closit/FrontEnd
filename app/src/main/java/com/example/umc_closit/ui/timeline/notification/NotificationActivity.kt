package com.example.umc_closit.ui.timeline.notification

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.timeline.NotificationDeleteResponse
import com.example.umc_closit.data.remote.timeline.NotificationResponse
import com.example.umc_closit.databinding.ActivityNotificationBinding
import com.example.umc_closit.utils.TokenUtils

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var newAdapter: NotificationAdapter
    private lateinit var lastAdapter: NotificationAdapter

    private var currentPage = 0 // 페이지네이션 관련 변수
    private var isLoading = false
    private var hasNextPage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { onBackPressed() }

        newAdapter = NotificationAdapter(mutableListOf())
        lastAdapter = NotificationAdapter(mutableListOf())

        binding.recyclerViewNew.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNew.adapter = newAdapter
        binding.recyclerViewLast.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewLast.adapter = lastAdapter

        setSwipeToDelete(newAdapter, binding.recyclerViewNew)
        setSwipeToDelete(lastAdapter, binding.recyclerViewLast)

        // 스크롤 이벤트 감지해서 다음 페이지 불러오기
        binding.recyclerViewNew.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val itemCount = layoutManager.itemCount

                if (!isLoading && hasNextPage && lastVisibleItemPosition == itemCount - 1) {
                    fetchNotifications()
                }
            }
        })

        fetchNotifications()
    }

    private fun fetchNotifications() {
        if (isLoading || !hasNextPage) return

        isLoading = true

        val apiCall = {
            RetrofitClient.timelineService.getNotifications(currentPage)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response: NotificationResponse ->
                if (response.isSuccess) {
                    val notifications = response.result.notiPreviewDTOList
                    val newItems = notifications.filter { !it.read }
                    val lastItems = notifications.filter { it.read }

                    newAdapter.addItems(newItems)
                    lastAdapter.addItems(lastItems)

                    updateEmptyViewVisibility(
                        newAdapter.itemCount == 0,
                        lastAdapter.itemCount == 0
                    )

                    hasNextPage = response.result.hasNext
                    currentPage++
                } else {
                    Toast.makeText(this, "알림 불러오기 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
                isLoading = false
            },
            onFailure = { t ->
                Toast.makeText(this, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            },
            retryCall = apiCall,
            context = this
        )
    }

    private fun updateEmptyViewVisibility(isNewEmpty: Boolean, isLastEmpty: Boolean) {
        binding.tvNewNone.visibility = if (isNewEmpty) View.VISIBLE else View.GONE
        binding.tvLastNone.visibility = if (isLastEmpty) View.VISIBLE else View.GONE
    }


    private fun deleteNotification(notificationId: Int) {
        val token = TokenUtils.getAccessToken(this) ?: return

        val apiCall = {
            RetrofitClient.timelineService.deleteNotification(notificationId)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response: NotificationDeleteResponse ->
                if (response.isSuccess) {
                    val newPosition = newAdapter.findPositionById(notificationId)
                    if (newPosition != -1) {
                        newAdapter.removeItem(newPosition)
                    } else {
                        val lastPosition = lastAdapter.findPositionById(notificationId)
                        if (lastPosition != -1) {
                            lastAdapter.removeItem(lastPosition)
                        }
                    }

                    // 삭제 후 리스트 상태에 따라 empty view 업데이트
                    updateEmptyViewVisibility(
                        newAdapter.itemCount == 0,
                        lastAdapter.itemCount == 0
                    )

                } else {
                    Toast.makeText(this, "알림 삭제 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "알림 삭제 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }

    private fun setSwipeToDelete(adapter: NotificationAdapter, recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(
            NotificationSwipeCallback(adapter) { notificationId ->
                deleteNotification(notificationId)
            }
        )
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}
