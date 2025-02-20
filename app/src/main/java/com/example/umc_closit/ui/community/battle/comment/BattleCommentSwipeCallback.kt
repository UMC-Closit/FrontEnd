package com.example.umc_closit.ui.battle.comment

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

class BattleCommentSwipeCallback(
    private val adapter: BattleCommentAdapter,
    private val myClositId: String // 내 ID 비교용
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        adapter.removeItem(position)
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // 내 댓글만 스와이프 허용
        val position = viewHolder.bindingAdapterPosition
        return if (adapter.isUserComment(position, myClositId)) {
            super.getSwipeDirs(recyclerView, viewHolder)
        } else {
            0 // 스와이프 막기
        }
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
        val backgroundColor = ContextCompat.getColor(itemView.context, R.color.pink_point)
        val background = ColorDrawable(backgroundColor)

        val icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)!!

        // 배경 설정
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)

        // 아이콘 크기를 아이템 높이의 70%로 설정
        val itemHeight = itemView.height
        val iconSize = (itemHeight * 0.6).toInt()
        val iconMargin = (itemHeight - iconSize) / 2

        val iconTop = itemView.top + iconMargin
        val iconBottom = iconTop + iconSize
        val iconLeft = itemView.right - iconMargin - iconSize
        val iconRight = itemView.right - iconMargin

        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
