package com.example.umc_closit.ui.timeline.comment

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.utils.TokenUtils

class CommentSwipeCallback(private val adapter: CommentAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = (viewHolder as CommentAdapter.CommentViewHolder).bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            adapter.removeItem(position)
        }
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val position = (viewHolder as CommentAdapter.CommentViewHolder).bindingAdapterPosition
        val currentUserId = TokenUtils.getClositId(viewHolder.itemView.context)?: ""

        return if (position != RecyclerView.NO_POSITION && adapter.isUserComment(position, currentUserId)) {
            super.getSwipeDirs(recyclerView, viewHolder)
        } else {
            0 // 슬라이드 방지
        }
    }


}
