package com.example.umc_closit.ui.timeline.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.data.remote.timeline.NotificationItem
import com.example.umc_closit.databinding.ItemNotificationBinding

class NotificationAdapter(
    private var items: MutableList<NotificationItem>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvMessage.text = "${item.content}"
            Glide.with(root.context)
                .load(item.imageUrl)
                .circleCrop() // 동그랗게 만드는 부분 추가
                .into(ivIcon)
        }
    }

    fun updateItems(newItems: List<NotificationItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getItemIdAtPosition(position: Int): Int {
        return items[position].notificationId
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun findPositionById(notificationId: Int): Int {
        return items.indexOfFirst { it.notificationId == notificationId }
    }

    fun addItems(newItems: List<NotificationItem>) {
        val startPosition = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }


    override fun getItemCount(): Int = items.size
}
