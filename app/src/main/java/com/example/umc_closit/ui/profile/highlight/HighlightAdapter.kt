package com.example.umc_closit.ui.profile.highlight

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.databinding.ItemAddHighlightBinding
import com.example.umc_closit.databinding.ItemHighlightBinding
import com.example.umc_closit.data.HighlightItem
import com.example.umc_closit.utils.DateUtils.getCurrentDate

class HighlightAdapter(
    private var items: MutableList<HighlightItem>,
    private val onAddClick: () -> Unit,
    private val screenWidth: Int // 화면 너비를 전달받음
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ADD = 0
        private const val VIEW_TYPE_HIGHLIGHT = 1
    }

    // "+" 버튼 ViewHolder
    class AddHighlightViewHolder(private val binding: ItemAddHighlightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(onAddClick: () -> Unit, itemSize: Int) {
            binding.ivAddHighlight.layoutParams = binding.ivAddHighlight.layoutParams.apply {
                width = itemSize
                height = itemSize
            }
            binding.ivAddHighlight.requestLayout() // 크기 갱신
            binding.tvAddHighlightDate.text = getCurrentDate()
            binding.ivAddHighlight.setOnClickListener { onAddClick() }
        }
    }

    // 하이라이트 ViewHolder
    class HighlightViewHolder(private val binding: ItemHighlightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HighlightItem, itemSize: Int) {
            binding.highlightImage.setImageResource(item.imageResId)
            binding.highlightText.text = item.date
            binding.highlightImage.layoutParams = binding.highlightImage.layoutParams.apply {
                width = itemSize
                height = itemSize
            }
            binding.highlightImage.requestLayout() // 크기 갱신
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ADD else VIEW_TYPE_HIGHLIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ADD) {
            val binding = ItemAddHighlightBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            AddHighlightViewHolder(binding)
        } else {
            val binding = ItemHighlightBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            HighlightViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemSize = (screenWidth * 0.14).toInt() // 화면 너비의 10%
        if (holder is AddHighlightViewHolder) {
            holder.bind(onAddClick, itemSize)
        } else if (holder is HighlightViewHolder) {
            val item = items[position - 1]
            holder.bind(item, itemSize)
        }
    }

    override fun getItemCount(): Int = items.size + 1 // "+" 버튼 포함

    // 데이터 추가 함수 (최대 5개 제한)
    fun updateItems(newItem: HighlightItem) {
        if (items.size < 5) {
            items.add(newItem)
            notifyItemInserted(items.size) // RecyclerView에 변경 알림
        }
    }

}
