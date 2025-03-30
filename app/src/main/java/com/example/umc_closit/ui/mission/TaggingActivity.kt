package com.example.umc_closit.ui.mission

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded
import com.example.umc_closit.databinding.ActivityTaggingBinding
import com.example.umc_closit.databinding.CustomTagDialogBinding
import java.util.ArrayList
import com.example.umc_closit.data.remote.post.TagData

class TaggingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaggingBinding
    private var originalBitmap: Bitmap? = null

    val tagList = mutableListOf<TagData>() // 태그 데이터 저장 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🚀 View Binding 초기화
        binding = ActivityTaggingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoPath = intent.getStringExtra("photoPath")
        photoPath?.let { path ->
            originalBitmap = rotateBitmapIfNeeded(path)
            originalBitmap?.let { bmp ->
                binding.imageViewTag.setImageBitmap(bmp)
            }
        }

        binding.imageViewTag.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val xRatio = event.x / binding.imageViewTag.width.toFloat()
                val yRatio = event.y / binding.imageViewTag.height.toFloat()

                showTagDialog { fullTag, displayTag ->
                    addTagView(fullTag, displayTag, xRatio, yRatio)
                }
            }
            true
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            val tagArrayList = ArrayList(tagList) // Intent로 넘기기 위해 ArrayList로 변환
            val resultIntent = intent.apply {
                putParcelableArrayListExtra("tagList", tagArrayList)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        tagList.forEach { tag ->
            val displayTag = if (tag.tagText.length > 7) tag.tagText.substring(0, 7) + "..." else tag.tagText
            addTagView(tag.tagText, displayTag, tag.xRatio, tag.yRatio)
        }
    }

    private fun showTagDialog(onTagSaved: (String, String) -> Unit) {
        val dialog = Dialog(this)
        val binding = CustomTagDialogBinding.inflate(layoutInflater)

        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명 처리

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            val fullTag = binding.etHashtag.text.toString().trim()

            if (fullTag.isNotEmpty()) {
                val displayTag = if (fullTag.length > 7) fullTag.substring(0, 7) + "..." else fullTag
                onTagSaved(fullTag, displayTag) // 원본 태그와 표시 태그 전달
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun addTagView(fullTag: String, displayTag: String, xRatio: Float, yRatio: Float) {
        tagList.add(TagData(xRatio, yRatio, fullTag)) // 전체 태그 저장

        val tagView = TextView(this).apply {
            text = displayTag
            setTextColor(Color.WHITE)
            textSize = 14f
            setBackgroundResource(com.example.umc_closit.R.drawable.bg_hashtag)
            val leftPad = dpToPx(30)
            val pad = dpToPx(8)
            setPadding(leftPad, pad, pad, pad)
            id = View.generateViewId()

            setOnClickListener {
                Toast.makeText(context, "전체 태그: $fullTag", Toast.LENGTH_SHORT).show()
            }
        }

        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        // `imageAndTag` 크기를 기준으로 실제 위치 계산
        val parentWidth = binding.imageAndTag.width.toFloat()
        val parentHeight = binding.imageAndTag.height.toFloat()

        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.leftMargin = (parentWidth * xRatio).toInt()
        layoutParams.topMargin = (parentHeight * yRatio).toInt()

        binding.imageAndTag.addView(tagView, layoutParams)
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}
