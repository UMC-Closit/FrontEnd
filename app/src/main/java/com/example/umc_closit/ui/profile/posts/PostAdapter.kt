package com.example.umc_closit.ui.profile.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.umc_closit.data.entities.Post
import com.example.umc_closit.databinding.ItemPostBinding
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.timeline.BookmarkDeleteResponse
import com.example.umc_closit.utils.TokenUtils
import android.content.Context

class PostAdapter(private val posts: MutableList<Post>, private val context: Context) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val selectedPosts = mutableListOf<Post>()  // 선택된 항목 저장
    private val timelineService = RetrofitClient.timelineService

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
            Glide.with(imgPost.context)
                .load(post.imageUrl)  // 이미지 URL 사용
                .into(imgPost)

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
        selectedPosts.forEach { post ->
            val apiCall = { timelineService.removeBookmark(post.postId) }  // post.id를 서버에 전달

            TokenUtils.handleTokenRefresh(
                call = apiCall(),
                onSuccess = { response: BookmarkDeleteResponse ->
                    if (response.isSuccess) {
                        posts.remove(post)
                        notifyDataSetChanged()
                        Toast.makeText(context, "북마크가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "북마크 삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = { t ->
                    Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                },
                retryCall = apiCall,
                context = context
            )
        }
        selectedPosts.clear()
    }

    fun resetSelection() {
        selectedPosts.clear()
        notifyDataSetChanged()
    }

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

}