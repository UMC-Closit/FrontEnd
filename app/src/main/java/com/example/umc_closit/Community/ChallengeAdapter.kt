package com.example.umc_closit.Community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

class ChallengeAdapter(private val itemList: List<String>) :
    RecyclerView.Adapter<ChallengeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val leftItem: View = view.findViewById(R.id.left_item)
        val rightItem: View = view.findViewById(R.id.right_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_challenge, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 아이템 데이터 바인딩 로직 추가 가능 (예: 이미지 설정, 클릭 리스너 추가 등)
    }

    override fun getItemCount(): Int = itemList.size
}
