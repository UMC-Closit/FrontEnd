package com.example.umc_closit.ui.community.battle

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.post.RecentPostResponse
import com.example.umc_closit.data.remote.post.UserRecentPostDTO
import com.example.umc_closit.databinding.ActivityNewbattleBinding
import com.example.umc_closit.utils.TokenUtils
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response


class NewBattleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewbattleBinding
    private lateinit var adapter: BattleAdapter
    private val itemList = mutableListOf<UserRecentPostDTO>()  // thumbnail URL 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewbattleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.ivBack.setOnClickListener {
            onBackPressed()  // 뒤로 가기
        }
        val myClositId = TokenUtils.getClositId(this) ?: ""
        fetchRecentPosts(myClositId)
    }

    private fun setupRecyclerView() {
        adapter = BattleAdapter(itemList, this)
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
                            Toast.makeText(this@NewBattleActivity, "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@NewBattleActivity, "API 실패: ${recentPostResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API_ERROR", "응답 실패: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@NewBattleActivity, "불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<RecentPostResponse>, t: Throwable) {
                Log.e("API_ERROR", "네트워크 오류: ${t.message}")
                Toast.makeText(this@NewBattleActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
