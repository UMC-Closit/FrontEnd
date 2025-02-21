package com.example.umc_closit.ui.community.challenge

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.battle.ChallengeBattlePreview
import com.example.umc_closit.data.remote.post.RecentPostResponse
import com.example.umc_closit.data.remote.post.UserRecentPostDTO
import com.example.umc_closit.databinding.ActivityMakechallengeBinding
import com.example.umc_closit.utils.TokenUtils
import kotlinx.coroutines.launch

class NewChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMakechallengeBinding
    private lateinit var adapter: NewChallengeAdapter
    private val itemList = mutableListOf<UserRecentPostDTO>()  // 최근 게시물 리스트

    private lateinit var challengeData: ChallengeBattlePreview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakechallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        challengeData = intent.getParcelableExtra("challenge_data") ?: return

        setupRecyclerView()

        binding.ivBack.setOnClickListener {
            onBackPressed()  // 뒤로 가기
        }

        val myClositId = TokenUtils.getClositId(this) ?: ""
        fetchRecentPosts(myClositId)
    }

    private fun setupRecyclerView() {
        adapter = NewChallengeAdapter(itemList, this, challengeData = intent.getParcelableExtra("challenge_data") ?: return)
        binding.challengeRecyclerView.layoutManager = GridLayoutManager(this, 3) // 한 줄에 3개
        binding.challengeRecyclerView.adapter = adapter
    }

    private fun fetchRecentPosts(clositId: String) {
        // Call 객체 생성
        val call = RetrofitClient.postService.getRecentPosts(clositId, 0)

        // 비동기 호출
        call.enqueue(object : retrofit2.Callback<RecentPostResponse> {
            override fun onResponse(
                call: retrofit2.Call<RecentPostResponse>,
                response: retrofit2.Response<RecentPostResponse>
            ) {
                if (response.isSuccessful) {
                    val recentPostResponse = response.body()
                    if (recentPostResponse != null && recentPostResponse.isSuccess) {
                        val posts = recentPostResponse.result.userRecentPostDTOList
                        if (posts.isNotEmpty()) {
                            itemList.clear()
                            itemList.addAll(posts)
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(this@NewChallengeActivity, "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@NewChallengeActivity, "API 실패: ${recentPostResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API_ERROR", "응답 실패: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@NewChallengeActivity, "불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<RecentPostResponse>, t: Throwable) {
                Log.e("API_ERROR", "네트워크 오류: ${t.message}")
                Toast.makeText(this@NewChallengeActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
