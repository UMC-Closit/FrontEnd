package com.example.umc_closit.Community

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R


class ChallengeAdapter(private val itemList: List<String>, private val context: Context) :
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
        // 오른쪽 아이템 클릭 시 NewChallengeActivity로 이동
        holder.rightItem.setOnClickListener {
            val intent = Intent(context, NewChallengeActivity::class.java)
            intent.putExtra("ITEM_POSITION", position) // 클릭한 아이템의 위치 정보 전달 가능
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = itemList.size
}
