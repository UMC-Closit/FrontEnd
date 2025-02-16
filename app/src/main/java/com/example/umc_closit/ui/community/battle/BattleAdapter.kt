package com.example.umc_closit.ui.community.battle

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.ui.community.battle.NewBattleDetailActivity
import com.example.umc_closit.databinding.ItemBattleBinding

class BattleAdapter(
    private val itemList: List<Int>,
    private val context: Context
) : RecyclerView.Adapter<BattleAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemBattleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageResId: Int, position: Int, context: Context) {
            binding.imgBattleIcon.setImageResource(imageResId)

            // 클릭 이벤트 추가 (이미지를 클릭하면 NewBattleDetailActivity로 이동)
            binding.imgBattleIcon.setOnClickListener {
                val intent = Intent(context, NewBattleDetailActivity::class.java).apply {
                    putExtra("ITEM_POSITION", position) // 클릭한 아이템의 위치 정보 전달
                    putExtra("IMAGE_RES_ID", imageResId) // 클릭한 이미지 리소스 전달
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBattleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], position, context)
    }

    override fun getItemCount(): Int = itemList.size
}