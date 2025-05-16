package com.example.umc_closit.ui.profile.recent

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.databinding.ActivityRecentDetailBinding
import com.example.umc_closit.utils.TokenUtils

class RecentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecentDetailBinding
    private lateinit var pagerAdapter: RecentDetailPagerAdapter
    private var postIdList: List<Int> = emptyList()
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }

    private fun setupViewPager() {
        pagerAdapter = RecentDetailPagerAdapter(this, postIdList)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.setCurrentItem(currentPosition, false)

        updateArrowVisibility()
    }

    private fun updateArrowVisibility() {
        binding.ivPrev.visibility = if (currentPosition == 0) View.GONE else View.VISIBLE
        binding.ivNext.visibility = if (currentPosition == postIdList.size - 1) View.GONE else View.VISIBLE
    }
}
