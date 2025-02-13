package com.example.umc_closit.ui.profile.highlight

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.databinding.ItemAddHighlightBinding
import com.example.umc_closit.databinding.ItemHighlightBinding
import com.example.umc_closit.data.entities.HighlightItem
import com.example.umc_closit.utils.DateUtils.getCurrentDate

class HighlightAdapter(
    private var items: MutableList<HighlightItem>,
    private val onAddClick: () -> Unit,
    private val screenWidth: Int, // 화면 너비
    private val isMyProfile: Boolean // 본인 프로필 여부
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ADD = 0
        private const val VIEW_TYPE_HIGHLIGHT = 1
    }

    class AddHighlightViewHolder(private val binding: ItemAddHighlightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(onAddClick: () -> Unit, itemSize: Int) {
            binding.ivAddHighlight.layoutParams = binding.ivAddHighlight.layoutParams.apply {
                width = itemSize
                height = itemSize
            }
            binding.ivAddHighlight.requestLayout()
            binding.tvAddHighlightDate.text = getCurrentDate()
            binding.ivAddHighlight.setOnClickListener { onAddClick() }
        }
    }

    class HighlightViewHolder(private val binding: ItemHighlightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HighlightItem, itemSize: Int) {
            binding.highlightImage.setImageResource(item.imageResId)
            binding.highlightText.text = item.date
            binding.highlightImage.layoutParams = binding.highlightImage.layoutParams.apply {
                width = itemSize
                height = itemSize
            }
            binding.highlightImage.requestLayout()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isMyProfile && position == 0) VIEW_TYPE_ADD else VIEW_TYPE_HIGHLIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ADD) {
            val binding = ItemAddHighlightBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            AddHighlightViewHolder(binding)
        } else {
            val binding = ItemHighlightBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            HighlightViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemSize = (screenWidth * 0.14).toInt()

        if (holder is AddHighlightViewHolder) {
            holder.bind(onAddClick, itemSize)
        } else if (holder is HighlightViewHolder) {
            val itemPosition = if (isMyProfile) position - 1 else position
            val item = items[itemPosition]
            holder.bind(item, itemSize)
        }
    }

    override fun getItemCount(): Int {
        return if (isMyProfile) items.size + 1 else items.size
    }

    fun updateItems(newItem: HighlightItem) {
        items.add(newItem)
        notifyItemInserted(items.size)
    }
}
