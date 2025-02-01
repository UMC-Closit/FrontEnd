package com.example.umc_closit.Community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

class BattleAdapter(private val battleList: List<Int>) : // Int로 변경 (이미지 리소스 ID 리스트)
    RecyclerView.Adapter<BattleAdapter.BattleViewHolder>() {

    class BattleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val battleIcon: ImageView = view.findViewById(R.id.img_battle_icon) // ImageView로 변경
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BattleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_battle, parent, false)
        return BattleViewHolder(view)
    }

    override fun onBindViewHolder(holder: BattleViewHolder, position: Int) {
        holder.battleIcon.setImageResource(battleList[position]) // 이미지 설정
    }

    override fun getItemCount() = battleList.size
}
