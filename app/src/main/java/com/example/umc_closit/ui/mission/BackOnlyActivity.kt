package com.example.umc_closit.ui.mission

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded
import com.example.umc_closit.databinding.ActivityBackOnlyBinding
import com.example.umc_closit.ui.timeline.TimelineActivity

class BackOnlyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackOnlyBinding
    private var originalBitmapPath: String? = null
    private var originalBitmap: Bitmap? = null

    companion object {
        private const val TAGGING_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBackOnlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        val backPhotoPath = intent.getStringExtra("backPhotoPath")
        backPhotoPath?.let { path ->
            originalBitmap = rotateBitmapIfNeeded(path)
            originalBitmap?.let { bmp ->
                binding.imageViewBackOnly.setImageBitmap(bmp)
            }
        }

        var isColorExtractMode = false

        binding.viewColorIcon.setOnClickListener {
            isColorExtractMode = !isColorExtractMode
        }

        binding.imageViewBackOnly.setOnTouchListener { _, event ->
            if (isColorExtractMode) {
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    originalBitmap?.let { bmp ->
                        val color = getTouchedColor(bmp, event.x, event.y)
                        setIconColor(binding.viewColorIcon, color)
                    }
                    isColorExtractMode = false
                }
            } else {
                if (event.action == MotionEvent.ACTION_UP) {
                    val intent = Intent(this, TaggingActivity::class.java).apply {
                        putExtra("photoPath", backPhotoPath)
                    }
                    startActivityForResult(intent, TAGGING_REQUEST_CODE)
                }
            }
            true
        }

        // 해시태그 버튼 클릭
        binding.btnHashtag.setOnClickListener {
            showHashtagDialog(
                currentHashtag = null,
                onHashtagSaved = { newHashtag ->
                    addHashtagToContainer(newHashtag)
                }
            )
        }

        binding.btnUpload.setOnClickListener {
            Toast.makeText(this, "미션 완료!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, TimelineActivity::class.java)
            intent.putExtra("showUploadFragment", true)
            startActivity(intent)
        }
    }

    private fun addHashtagToContainer(hashtag: String) {
        val hashtagView = android.widget.TextView(this).apply {
            text = "#$hashtag"
            setBackgroundResource(com.example.umc_closit.R.drawable.bg_detail_hashtag)
            setTextColor(resources.getColor(android.R.color.white))
            setPadding(10, 10, 10, 10)
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 10
                marginEnd = 10
            }
        }
        binding.hashtagContainer.addView(hashtagView)
    }

    private fun showHashtagDialog(
        currentHashtag: String?,
        onHashtagSaved: (String) -> Unit
    ) {
        val editText = android.widget.EditText(this).apply {
            hint = "#해시태그 입력"
            currentHashtag?.let { setText(it) }
        }

        AlertDialog.Builder(this)
            .setTitle("해시태그 ${if (currentHashtag == null) "입력" else "수정"}")
            .setView(editText)
            .setPositiveButton("확인") { _: DialogInterface, _: Int ->
                val input = editText.text.toString()
                if (input.isNotBlank()) {
                    onHashtagSaved(input)
                }
            }
            .setNegativeButton("취소", null)
            .show()
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
        val ivWidth = binding.imageViewBackOnly.width
        val ivHeight = binding.imageViewBackOnly.height

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
                    binding.imageViewBackOnly.setImageBitmap(bmp)
                }
            }
        }
    }
}