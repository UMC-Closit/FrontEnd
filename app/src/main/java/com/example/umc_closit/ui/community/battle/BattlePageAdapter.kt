package com.example.umc_closit.ui.community.battle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.databinding.ItemBattleMainBinding

class BattlePageAdapter(private val itemList: List<String>) :
    RecyclerView.Adapter<BattlePageAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemBattleMainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val leftItem = binding.leftItem
        val rightItem = binding.rightItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBattleMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 아이템 데이터 바인딩 로직 추가 가능 (예: 이미지 설정, 클릭 리스너 추가 등)
    }

    override fun getItemCount(): Int = itemList.size
}