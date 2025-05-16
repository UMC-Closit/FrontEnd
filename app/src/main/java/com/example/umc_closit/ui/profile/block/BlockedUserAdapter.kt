package com.example.umc_closit.ui.profile.block

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.data.remote.profile.BlockedUser
import com.example.umc_closit.databinding.ItemBlockedUserBinding

class BlockedUserAdapter(
    private val items: List<BlockedUser>,
    private val onUnblockClick: (BlockedUser) -> Unit
) : RecyclerView.Adapter<BlockedUserAdapter.BlockedUserViewHolder>() {

    inner class BlockedUserViewHolder(val binding: ItemBlockedUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedUserViewHolder {
        val binding = ItemBlockedUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlockedUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlockedUserViewHolder, position: Int) {
        val user = items[position]
        with(holder.binding) {
/*            tvUserName.text = user.name
            Glide.with(root.context).load(user.profileImage).circleCrop().into(ivProfile)
            btnUnblock.setOnClickListener { onUnblockClick(user) }*/
        }
    }

    override fun getItemCount() = items.size
}