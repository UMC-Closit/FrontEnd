package com.example.umc_closit.ui.profile.posts

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.umc_closit.databinding.ActivitySavedPostsBinding
import com.example.umc_closit.data.entities.Post
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.BookmarkResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavedPostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedPostsBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivitySavedPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

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
        postAdapter = PostAdapter(mutableListOf(), this)
        binding.recyclerView.adapter = postAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)

        // API로부터 북마크된 게시물 가져오기
        getPostsFromApi()
    }

    private fun getPostsFromApi() {
        RetrofitClient.profileService.getBookmarks(page = 0, size = 20)
            .enqueue(object : Callback<BookmarkResponse> {
                override fun onResponse(call: Call<BookmarkResponse>, response: Response<BookmarkResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val bookmarks = response.body()?.result?.bookmarkResultDTOList ?: emptyList()

                        // BookmarkItem -> Post 변환
                        val posts = bookmarks.map {
                            Post(
                                title = "Post ${it.postId}",
                                imageUrl = it.thumbnail,  // API에서 받은 이미지 URL 사용
                                postId = it.postId
                            )
                        }.toMutableList()

                        postAdapter.updatePosts(posts)
                    } else {
                        Toast.makeText(this@SavedPostsActivity, "북마크 불러오기 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BookmarkResponse>, t: Throwable) {
                    Toast.makeText(this@SavedPostsActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
