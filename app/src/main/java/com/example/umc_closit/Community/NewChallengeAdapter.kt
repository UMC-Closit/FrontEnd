package com.example.umc_closit.Community

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R



class NewChallengeAdapter(
    private val itemList: List<Int>,
    private val context: Context
) : RecyclerView.Adapter<NewChallengeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.img_battle_icon) // item_battle.xml 내 이미지뷰
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_battle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(itemList[position])

        // 클릭 이벤트 추가 (이미지를 클릭하면 NewBattleDetailActivity로 이동)
        holder.imageView.setOnClickListener {
            val intent = Intent(context, NewChallengeDetailActivity::class.java).apply {
                putExtra("ITEM_POSITION", position) // 클릭한 아이템의 위치 정보 전달
                putExtra("IMAGE_RES_ID", itemList[position]) // 클릭한 이미지 리소스 전달
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = itemList.size
}
