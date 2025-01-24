package com.example.umc_closit.ui.profile.highlight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R
import com.example.umc_closit.data.HighlightItem

class HighlightAdapter(
    private var items: MutableList<HighlightItem>, // 가변 리스트 사용
    private val onAddClick: () -> Unit             // "+" 버튼 클릭 이벤트
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ADD = 0 // "+" 버튼 뷰 타입
        private const val VIEW_TYPE_HIGHLIGHT = 1 // 하이라이트 뷰 타입
    }

    // "+" 버튼 ViewHolder
    class AddHighlightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addButton: ImageView = view.findViewById(R.id.addHighlightImage)
    }

    // 하이라이트 ViewHolder
    class HighlightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.highlightImage)
        val dateTextView: TextView = view.findViewById(R.id.highlightDate)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ADD else VIEW_TYPE_HIGHLIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ADD) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_highlight, parent, false)
            AddHighlightViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_highlight, parent, false)
            HighlightViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddHighlightViewHolder) {
            // "+" 버튼 클릭 이벤트 설정
            holder.addButton.setOnClickListener { onAddClick() }
        } else if (holder is HighlightViewHolder) {
            // 하이라이트 데이터 바인딩
            val item = items[position - 1] // 첫 번째 아이템이 "+" 버튼이므로 -1
            holder.imageView.setImageResource(item.imageResId)
            holder.dateTextView.text = item.date
        }
    }

    override fun getItemCount(): Int = items.size + 1 // "+" 버튼 포함

    // 데이터 업데이트 함수
    fun updateItems(newItems: List<HighlightItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
