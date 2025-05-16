package com.example.umc_closit.ui.profile.highlight

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.databinding.ActivityHighlightDetailBinding
import com.example.umc_closit.utils.TokenUtils

class HighlightDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHighlightDetailBinding
    private lateinit var pagerAdapter: HighlightDetailPagerAdapter
    private var postIdList: List<Int> = emptyList()
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHighlightDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ postIdList랑 클릭된 위치 받기
        postIdList = intent.getIntegerArrayListExtra("postIdList") ?: arrayListOf()
        currentPosition = intent.getIntExtra("clickedPosition", 0)

        setupViewPager()

        binding.ivBack.setOnClickListener { finish() }
        binding.ivPrev.setOnClickListener {
            if (currentPosition > 0) binding.viewPager.currentItem = currentPosition - 1
        }
        binding.ivNext.setOnClickListener {
            if (currentPosition < postIdList.size - 1) binding.viewPager.currentItem = currentPosition + 1
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
                updateArrowVisibility()
            }
        })

        binding.ivDelete.setOnClickListener {
            val currentPostId = postIdList[currentPosition]

            deleteHighlight(currentPostId, onSuccess = {
                // 삭제 성공했으면 목록에서 제거
                val newList = postIdList.toMutableList().apply { removeAt(currentPosition) }

                if (newList.isEmpty()) {
                    // 삭제하고 남은 게 없다면 프로필 프래그먼트로 이동
                    finish()
                } else {
                    // 현재 페이지 삭제한 경우, 이전 페이지로 이동하도록 보정
                    currentPosition = if (currentPosition > 0) currentPosition - 1 else 0

                    // 뷰페이저 갱신
                    postIdList = newList
                    pagerAdapter.updateList(newList)
                    binding.viewPager.setCurrentItem(currentPosition, false)

                    // 화살표 다시 반영
                    updateArrowVisibility()
                }
            }, onFailure = { e ->
                Toast.makeText(this, "삭제 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            })
        }

    }

    private fun setupViewPager() {
        pagerAdapter = HighlightDetailPagerAdapter(this, postIdList)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.setCurrentItem(currentPosition, false)

        updateArrowVisibility()
    }

    private fun updateArrowVisibility() {
        binding.ivPrev.visibility = if (currentPosition == 0) View.GONE else View.VISIBLE
        binding.ivNext.visibility = if (currentPosition == postIdList.size - 1) View.GONE else View.VISIBLE
    }

    fun deleteHighlight(postId: Int, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        val apiCall = { RetrofitClient.profileService.deleteHighlight(postId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    onSuccess()
                } else {
                    onFailure(Exception("삭제 실패: ${response.message}"))
                }
            },
            onFailure = { t -> onFailure(t) },
            retryCall = apiCall,
            context = this
        )
    }



}
