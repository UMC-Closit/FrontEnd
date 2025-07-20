package com.example.umc_closit.ui.profile.highlight


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.post.PostDetail
import com.example.umc_closit.databinding.ActivityAddHighlightBinding
import com.example.umc_closit.utils.TokenUtils
import kotlinx.coroutines.launch
import kotlin.math.abs

class AddHighlightActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHighlightBinding
    private lateinit var viewPagerAdapter: PostDetailPagerAdapter
    private var postIdList: List<Int> = emptyList()
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHighlightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postIdList = intent.getIntegerArrayListExtra("postIdList") ?: arrayListOf()
        currentPosition = intent.getIntExtra("currentPosition", 0)

        viewPagerAdapter = PostDetailPagerAdapter(this, postIdList)
        binding.viewPager.adapter = viewPagerAdapter

        binding.viewPager.setPageTransformer { page, position ->
            page.alpha = 0.5f + (1 - abs(position)) * 0.5f
            page.scaleY = 0.9f + (1 - abs(position)) * 0.1f
        }


        if (postIdList.isNotEmpty()) {
            binding.viewPager.setCurrentItem(currentPosition, false)
        }

        updateArrowVisibility()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
                updateArrowVisibility()
            }
        })

        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.ivPrev.setOnClickListener {
            if (currentPosition > 0) {
                binding.viewPager.setCurrentItem(currentPosition - 1, true)
            }
        }

        binding.ivNext.setOnClickListener {
            if (currentPosition < postIdList.size - 1) {
                binding.viewPager.setCurrentItem(currentPosition + 1, true)
            }
        }
    }

    private fun updateArrowVisibility() {
        if (postIdList.size <= 1) {
            binding.ivPrev.visibility = View.GONE
            binding.ivNext.visibility = View.GONE
        } else {
            binding.ivPrev.visibility = if (currentPosition == 0) View.GONE else View.VISIBLE
            binding.ivNext.visibility = if (currentPosition == postIdList.size - 1) View.GONE else View.VISIBLE
        }
    }

}
