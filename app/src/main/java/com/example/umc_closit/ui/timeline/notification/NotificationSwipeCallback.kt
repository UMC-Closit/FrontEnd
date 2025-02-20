package com.example.umc_closit.ui.timeline.notification

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

class NotificationSwipeCallback(
    private val adapter: NotificationAdapter,
    private val onDelete: (Int) -> Unit // 알림 삭제 함수
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val notificationId = adapter.getItemIdAtPosition(position)
        onDelete(notificationId)
        adapter.removeItem(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val backgroundColor = ContextCompat.getColor(recyclerView.context, R.color.pink_point)
        val icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)

        // 배경색
        val background = ColorDrawable(backgroundColor)
        background.setBounds(
            itemView.right + dX.toInt(), itemView.top,
            itemView.right, itemView.bottom
        )
        background.draw(c)

        // 아이콘 위치 설정
        icon?.let {
            val itemHeight = itemView.height
            val iconSize = (itemHeight * 0.7).toInt() // 아이템 높이의 70%
            val iconMargin = (itemHeight - iconSize) / 2

            val iconLeft = itemView.right - iconMargin - iconSize
            val iconRight = itemView.right - iconMargin
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + iconSize

            it.setBounds(iconLeft, iconTop, iconRight, iconBottom)

            it.draw(c)
        }

        // 기본 스와이프 애니메이션
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
