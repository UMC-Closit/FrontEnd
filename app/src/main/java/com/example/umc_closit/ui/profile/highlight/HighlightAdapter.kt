package com.example.umc_closit.ui.profile.highlight

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc_closit.data.remote.profile.HighlightItem
import com.example.umc_closit.databinding.ItemAddHighlightBinding
import com.example.umc_closit.databinding.ItemHighlightBinding
import com.example.umc_closit.utils.DateUtils.getCurrentDate

class HighlightAdapter(
    private var items: MutableList<HighlightItem>,
    private val onAddClick: () -> Unit,
    private val onItemClick: (HighlightItem) -> Unit,
    private val screenWidth: Int,
    private val isMyProfile: Boolean
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
        fun bind(item: HighlightItem, itemSize: Int, onItemClick: (HighlightItem) -> Unit) {
            Glide.with(binding.root.context)
                .load(item.thumbnail)
                .into(binding.highlightImage)

            binding.highlightText.text = item.createdAt.substring(2, 10).replace("-", ".")

            binding.highlightImage.layoutParams = binding.highlightImage.layoutParams.apply {
                width = itemSize
                height = itemSize
            }
            binding.highlightImage.requestLayout()

            binding.root.setOnClickListener {
                onItemClick(item)
            }
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
            holder.bind(item, itemSize, onItemClick)
        }
    }

    override fun getItemCount(): Int {
        return if (isMyProfile) items.size + 1 else items.size
    }

    fun updateItems(newItems: List<HighlightItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItem(newItem: HighlightItem) {
        items.add(newItem)
        notifyItemInserted(items.size - 1)
    }

    fun getPostIdList(): List<Int> {
        return items.map { it.postId }
    }

    fun setItems(newItems: List<HighlightItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

}
