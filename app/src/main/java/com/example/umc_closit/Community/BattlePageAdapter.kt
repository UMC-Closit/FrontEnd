package com.example.umc_closit.Community

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

class BattlePageAdapter(private val itemList: List<String>) :
    RecyclerView.Adapter<BattlePageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val leftVoteButton: View = view.findViewById(R.id.btn_left_vote)
        val rightVoteButton: View = view.findViewById(R.id.btn_right_vote)
        val voteProgressBar: ProgressBar = view.findViewById(R.id.vote_progress_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_battle_main, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 임시로 투표 비율을 50:50으로 시작
        holder.voteProgressBar.progress = 50

        holder.leftVoteButton.setOnClickListener {
            Toast.makeText(it.context, "좌측에 투표했습니다!", Toast.LENGTH_SHORT).show()
            updateVoteProgress(holder.voteProgressBar, 70) // 임시 비율 70%
        }

        holder.rightVoteButton.setOnClickListener {
            Toast.makeText(it.context, "우측에 투표했습니다!", Toast.LENGTH_SHORT).show()
            updateVoteProgress(holder.voteProgressBar, 30) // 임시 비율 30%
        }
    }

    override fun getItemCount(): Int = itemList.size

    /**
     * 투표 비율 업데이트 애니메이션
     */
    private fun updateVoteProgress(progressBar: ProgressBar, newProgress: Int) {
        val animator = ValueAnimator.ofInt(progressBar.progress, newProgress)
        animator.duration = 500 // 0.5초 동안 애니메이션
        animator.addUpdateListener { animation ->
            progressBar.progress = animation.animatedValue as Int
        }
        animator.start()
    }
}
