package com.example.umc_closit.ui.profile.posts

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.umc_closit.databinding.ActivitySavedPostsBinding
import com.example.umc_closit.data.entities.Post

class SavedPostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedPostsBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivitySavedPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // btnTrash 클릭 시 동작
        binding.btnTrash.setOnClickListener {
            // btnTrash 숨기고 LinearLayout을 보이게
            binding.btnTrash.visibility = View.GONE
            binding.imageButtonLayout.visibility = View.VISIBLE
        }

        // btnDelete 클릭 시 동작
        binding.btnDelete.setOnClickListener {
            // 선택된 사진 삭제
            val selectedPosts = postAdapter.getSelectedPosts()
            if (selectedPosts.isNotEmpty()) {
                postAdapter.deleteSelectedPosts()
                Toast.makeText(this, "${selectedPosts.size} 개의 게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "삭제할 게시글을 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // btnCancel 클릭 시 동작
        binding.btnCancel.setOnClickListener {
            // 선택 해제 후 btnTrash와 LinearLayout 원래대로
            postAdapter.resetSelection()
            binding.btnTrash.visibility = View.VISIBLE
            binding.imageButtonLayout.visibility = View.GONE
        }

        // RecyclerView 설정
        postAdapter = PostAdapter(getPosts())
        binding.recyclerView.adapter = postAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
    }

    private fun getPosts(): MutableList<Post> {
        val posts = mutableListOf<Post>()
        for (i in 1..17) {
            posts.add(Post("Image $i", com.example.umc_closit.R.drawable.sample_image)) // TODO: 시연용, 추후 수정
        }
        return posts
    }
}