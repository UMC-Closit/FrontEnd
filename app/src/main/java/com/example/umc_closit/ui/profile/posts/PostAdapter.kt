package com.example.umc_closit.ui.profile.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.data.entities.Post
import com.example.umc_closit.databinding.ItemPostBinding

class PostAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val selectedPosts = mutableListOf<Post>()  // 선택된 항목 저장

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // 이미지 클릭 시 체크 표시 토글
            binding.root.setOnClickListener {
                val post = posts[adapterPosition]
                if (selectedPosts.contains(post)) {
                    selectedPosts.remove(post)  // 선택 해제
                    binding.imgCheck.visibility = View.GONE  // 체크 표시 숨기기
                } else {
                    selectedPosts.add(post)  // 선택
                    binding.imgCheck.visibility = View.VISIBLE  // 체크 표시 보이기
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {
            imgPost.setImageResource(post.imageResId)  // TODO: 시연용, 추후 수정
            imgCheck.visibility = if (selectedPosts.contains(post)) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun getSelectedPosts(): List<Post> {
        return selectedPosts
    }

    fun deleteSelectedPosts() {
        posts.removeAll(selectedPosts)
        selectedPosts.clear()
        notifyDataSetChanged()  // 변경 사항 반영
    }

    fun resetSelection() {
        selectedPosts.clear()
        notifyDataSetChanged()
    }
}