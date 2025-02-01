package com.example.umc_closit.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.MainActivity
import com.example.umc_closit.databinding.ActivitySplashBinding
import com.example.umc_closit.ui.timeline.TimelineActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔥 애니메이션 적용할 대상 (아이콘)
        val splashIcon = binding.ivSplashIcon

        // 📌 1. 페이드인 애니메이션 (alpha: 0 → 1)
        val fadeIn = ObjectAnimator.ofFloat(splashIcon, "alpha", 0f, 1f).apply {
            duration = 1000 // 1초 동안 실행
        }

        // 📌 2. 크기 확대 애니메이션 (scaleX, scaleY: 0.5 → 1.2 → 1)
        val scaleX = ObjectAnimator.ofFloat(splashIcon, "scaleX", 0.9f, 1f).apply {
            duration = 1000
        }
        val scaleY = ObjectAnimator.ofFloat(splashIcon, "scaleY", 0.9f, 1f).apply {
            duration = 1000
        }


        // 📌 3. 아래에서 위로 이동 (Y축)
        val moveUp = ObjectAnimator.ofFloat(splashIcon, "translationY", 80f, 0f).apply {
            duration = 1000
        }

        // 🔥 모든 애니메이션을 함께 실행
        AnimatorSet().apply {
            playTogether(fadeIn, scaleX, scaleY, moveUp)
            start()
        }

        // 3초 후 MainActivity로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, TimelineActivity::class.java))
            finish()
        }, 3000)
    }
}