package com.example.umc_closit.ui.profile.follow

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.Follow
import com.example.umc_closit.databinding.ActivityFollowListBinding
import com.example.umc_closit.ui.follow.FollowingAdapter
import com.example.umc_closit.utils.TokenUtils

class FollowListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFollowListBinding
    private val followingList = mutableListOf<Follow>()
    private val followersList = mutableListOf<Follow>()
    private var _isLoading = MutableLiveData<Boolean>(false)
    private var currentPage = 0
    private var hasNextPage = true
    private var profileUserClositId: String = ""  // 타 유저의 clositId
    private var listType: String = ""  // 팔로잉/팔로워 구분

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listType = intent.getStringExtra("listType") ?: "following" // 기본값: 팔로잉 목록
        profileUserClositId = intent.getStringExtra("profileUserClositId") ?: getLoggedInUserClositId()

        if (listType == "following") {
            fetchFollowingList()
            binding.tvTitle.text = "Following"
        } else if (listType == "followers") {
            fetchFollowersList()
            binding.tvTitle.text = "Followers"
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        // RecyclerView 설정
        binding.recyclerViewNew.apply {
            adapter = FollowingAdapter(followingList) // 기본 팔로잉 목록 어댑터 설정
            layoutManager = LinearLayoutManager(this@FollowListActivity)
        }

        binding.recyclerViewNew.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!recyclerView.canScrollVertically(1)) {
                    if (listType == "following") {
                        fetchFollowingList()
                    } else if (listType == "followers") {
                        fetchFollowersList()
                    }
                }
            }
        })
    }

    // 팔로잉 목록 조회
    private fun fetchFollowingList() {
        if (_isLoading.value == true || !hasNextPage) return

        _isLoading.value = true

        val apiCall = {
            RetrofitClient.profileService.getFollowingList(
                clositId = profileUserClositId,
                page = currentPage,
                size = 10
            )
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->

                if (response.isSuccess) {
                    val newFollowingList = response.result?.followings?.filterNotNull() ?: emptyList()
                    followingList.addAll(newFollowingList)

                    if (followingList.isEmpty()) {
                        binding.tvNewNone.visibility = View.VISIBLE
                        binding.tvNewNone.text = "팔로잉 목록이 없습니다"
                    } else {
                        binding.tvNewNone.visibility = View.GONE
                    }

                    hasNextPage = response.result?.hasNext ?: false
                    currentPage++

                    (binding.recyclerViewNew.adapter as? FollowingAdapter)?.notifyDataSetChanged()
                } else {
                    Log.d("FOLLOW", "API Call Failed: ${response.message}")
                }

                _isLoading.value = false
            },
            onFailure = { t ->
                _isLoading.value = false
                Toast.makeText(this, "팔로잉 목록 불러오기 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }

    // 팔로워 목록 조회
    private fun fetchFollowersList() {
        if (_isLoading.value == true || !hasNextPage) return

        _isLoading.value = true

        val apiCall = {
            RetrofitClient.profileService.getFollowersList(
                clositId = profileUserClositId,
                page = currentPage,
                size = 10
            )
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->

                if (response.isSuccess) {
                    val newFollowersList = response.result?.followers?.filterNotNull() ?: emptyList()
                    followersList.addAll(newFollowersList)

                    if (followersList.isEmpty()) {
                        binding.tvNewNone.visibility = View.VISIBLE
                        binding.tvNewNone.text = "팔로워 목록이 없습니다"
                    } else {
                        binding.tvNewNone.visibility = View.GONE
                    }

                    hasNextPage = response.result?.hasNext ?: false
                    currentPage++

                    (binding.recyclerViewNew.adapter as? FollowingAdapter)?.updateFollowingItems(followersList)
                } else {
                    Log.d("FOLLOW", "API Call Failed: ${response.message}")
                }

                _isLoading.value = false
            },
            onFailure = { t ->
                _isLoading.value = false
                Toast.makeText(this, "팔로워 목록 불러오기 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }

    private fun getLoggedInUserClositId(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("clositId", "") ?: ""
    }
}