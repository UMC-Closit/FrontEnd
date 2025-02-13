package com.example.umc_closit.ui.mission

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded
import com.example.umc_closit.databinding.ActivityFrontOnlyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FrontOnlyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFrontOnlyBinding

    private var frontPhotoPath: String? = null
    private var backPhotoPath: String? = null
    private var originalBitmapPath: String? = null

    private val hashtagsFlow = MutableStateFlow<List<String>>(emptyList())

    private var originalBitmap: Bitmap? = null

    companion object {
        private const val TAGGING_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🚀 View Binding 초기화
        binding = ActivityFrontOnlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 뒤로가기 버튼 설정
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        frontPhotoPath = intent.getStringExtra("frontPhotoPath")
        frontPhotoPath?.let { path ->
            originalBitmap = rotateBitmapIfNeeded(path)
            originalBitmap?.let { bmp ->
                binding.imageViewFrontOnly.setImageBitmap(bmp)
            }
        }

        backPhotoPath = intent.getStringExtra("backPhotoPath")

        var isColorExtractMode = false

        binding.viewColorIcon.setOnClickListener {
            isColorExtractMode = !isColorExtractMode
        }

        binding.imageViewFrontOnly.setOnTouchListener { view, event ->
            if (isColorExtractMode) {
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    originalBitmap?.let { bmp ->
                        val color = getTouchedColor(bmp, event.x, event.y)
                        setIconColor(binding.viewColorIcon, color)
                    }
                }
            } else {
                if (event.action == MotionEvent.ACTION_UP) {
                    val intent = Intent(this, TaggingActivity::class.java).apply {
                        putExtra("photoPath", frontPhotoPath)
                    }
                    startActivityForResult(intent, TAGGING_REQUEST_CODE)
                }
            }
            true
        }

        lifecycleScope.launch(Dispatchers.Main) {
            hashtagsFlow.collect { hashtags ->
                updateHashtagsUI(hashtags)
            }
        }

        // 해시태그 버튼
            binding.btnHashtag.setOnClickListener {
            showHashtagDialog { newHashtag ->
                addHashtag(newHashtag)
            }
        }

        // BackOnlyActivity로 이동
        binding.btnContinue.setOnClickListener {
            val intent = Intent(this, BackOnlyActivity::class.java).apply {
                putExtra("backPhotoPath", backPhotoPath)
            }
            startActivity(intent)
        }
    }

    private fun addHashtag(hashtag: String) {
        val currentHashtags = hashtagsFlow.value.toMutableList()
        currentHashtags.add(hashtag)
        hashtagsFlow.value = currentHashtags
    }

    // 해시태그 입력 다이얼로그
    private fun showHashtagDialog(onHashtagSaved: (String) -> Unit) {
        val editText = android.widget.EditText(this).apply {
            hint = "#해시태그 입력"
        }

        AlertDialog.Builder(this)
            .setTitle("해시태그 입력")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                val input = editText.text.toString()
                if (input.isNotBlank()) {
                    onHashtagSaved(input)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun updateHashtagsUI(hashtags: List<String>) {
        binding.hashtagContainer.removeAllViews()

        hashtags.forEach { hashtag ->
            val hashtagTextView = android.widget.TextView(this).apply {
                text = "#$hashtag"
                setTextColor(resources.getColor(com.example.umc_closit.R.color.white))
                setPadding(16, 8, 16, 8)
                background = resources.getDrawable(com.example.umc_closit.R.drawable.bg_detail_hashtag, null)
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 8
                }
            }

            binding.hashtagContainer.addView(hashtagTextView)
        }
    }

    // 아이콘 색상 변경
    private fun setIconColor(view: android.view.View, color: Int) {
        val bg = view.background
        if (bg is GradientDrawable) {
            bg.setColor(color)
        } else {
            view.setBackgroundColor(color)
        }
    }

    // 이미지에서 색상 추출
    private fun getTouchedColor(bitmap: Bitmap, touchX: Float, touchY: Float): Int {
        val ivWidth = binding.imageViewFrontOnly.width
        val ivHeight = binding.imageViewFrontOnly.height

        val bmpWidth = bitmap.width
        val bmpHeight = bitmap.height

        val xRatio = touchX / ivWidth
        val yRatio = touchY / ivHeight

        val pixelX = (xRatio * bmpWidth).toInt().coerceIn(0, bmpWidth - 1)
        val pixelY = (yRatio * bmpHeight).toInt().coerceIn(0, bmpHeight - 1)

        return bitmap.getPixel(pixelX, pixelY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAGGING_REQUEST_CODE && resultCode == RESULT_OK) {
            val taggedPhotoPath = data?.getStringExtra("taggedPhotoPath")
            if (!taggedPhotoPath.isNullOrEmpty()) {
                originalBitmapPath = taggedPhotoPath
                val bmp = BitmapFactory.decodeFile(taggedPhotoPath)
                if (bmp != null) {
                    binding.imageViewFrontOnly.setImageBitmap(bmp)
                }
            }
        }
    }
}