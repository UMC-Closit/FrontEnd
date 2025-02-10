package com.example.umc_closit.ui.community.challenge

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.databinding.ItemChallengeBinding

class ChallengeAdapter(private val itemList: List<String>, private val context: Context) :
    RecyclerView.Adapter<ChallengeAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemChallengeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val leftItem = binding.leftItem.root
        val rightItem = binding.rightItem.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChallengeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 오른쪽 아이템 클릭 시 NewChallengeActivity로 이동
        holder.rightItem.setOnClickListener {
            val intent = Intent(context, NewChallengeActivity::class.java)
            intent.putExtra("ITEM_POSITION", position) // 클릭한 아이템의 위치 정보 전달 가능
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = itemList.size
}