package com.example.umc_closit.ui.timeline.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R
import com.example.umc_closit.databinding.ActivityNotificationBinding
import com.example.umc_closit.databinding.ItemNotificationBinding

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()  // 또는 finish()를 사용하여 액티비티 종료
        }

        // New notifications
        val newNotifications = listOf(
            NotificationItem("000님이 좋아요를 눌렀습니다.", R.drawable.ex_noti_img),
            NotificationItem("000님이 팔로우하였습니다.", R.drawable.ex_noti_profile)
        )

        // Last notifications
        val lastNotifications = listOf(
            NotificationItem("000님이 댓글을 남겼습니다.", R.drawable.ex_noti_img),
            NotificationItem("000님이 좋아요를 눌렀습니다.", R.drawable.ex_noti_img)
        )

        // Set adapters for RecyclerViews
        val newAdapter = NotificationAdapter(newNotifications)
        val lastAdapter = NotificationAdapter(lastNotifications)

        binding.recyclerViewNew.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNew.adapter = newAdapter

        binding.recyclerViewLast.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewLast.adapter = lastAdapter
    }
}

data class NotificationItem(val message: String, val iconResId: Int)

class NotificationAdapter(private val items: List<NotificationItem>) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvMessage.text = item.message
            ivIcon.setImageResource(item.iconResId)
        }
    }

    override fun getItemCount(): Int = items.size
}