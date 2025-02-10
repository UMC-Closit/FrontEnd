package com.example.umc_closit.ui.mission

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded
import com.example.umc_closit.databinding.ActivityTaggingBinding
import com.example.umc_closit.databinding.CustomTagDialogBinding
import java.io.File
import java.io.FileOutputStream

class TaggingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaggingBinding
    private var originalBitmap: Bitmap? = null

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
                val localX = event.x
                val localY = event.y
                showTagDialog { tagText ->
                    addTagView(tagText, localX, localY)
                }
            }
            true
        }

        binding.btnSave.setOnClickListener {
            val savedFile = captureAndSaveTaggedImage()
            savedFile?.let {
                val resultIntent = intent.apply {
                    putExtra("taggedPhotoPath", it.absolutePath)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun showTagDialog(onTagSaved: (String) -> Unit) {
        val dialog = Dialog(this)
        val binding = CustomTagDialogBinding.inflate(layoutInflater)

        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명 처리

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            val input = binding.etHashtag.text.toString().trim()
            if (input.isNotEmpty()) {
                onTagSaved(input)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun addTagView(tagText: String, localX: Float, localY: Float) {
        val tagView = android.widget.TextView(this).apply {
            text = tagText
            setTextColor(Color.WHITE)
            textSize = 14f
            setBackgroundResource(com.example.umc_closit.R.drawable.bg_hashtag)
            val leftPad = dpToPx(30)
            val pad = dpToPx(8)
            setPadding(leftPad, pad, pad, pad)
            id = View.generateViewId() // ✅ ConstraintLayout에서 동적 뷰 추가를 위해 ID 생성
        }

        // ✅ `imageViewTag` 내에서 상대적인 위치를 계산
        val imageViewX = binding.imageViewTag.x
        val imageViewY = binding.imageViewTag.y

        val finalX = imageViewX + localX
        val finalY = imageViewY + localY

        // ✅ ConstraintLayout.LayoutParams 적용
        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.leftMargin = finalX.toInt()
        params.topMargin = finalY.toInt()

        // ✅ ConstraintLayout에 추가
        binding.taggingLayout.addView(tagView, params)
    }

    private fun captureAndSaveTaggedImage(): File? {
        val originalVisibility = binding.btnSave.visibility
        binding.btnSave.visibility = View.GONE

        val width = binding.taggingLayout.width
        val height = binding.taggingLayout.height
        if (width <= 0 || height <= 0) {
            binding.btnSave.visibility = originalVisibility
            return null
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        binding.taggingLayout.draw(canvas)

        val file = File(filesDir, "tagged_${System.currentTimeMillis()}.jpg")
        return try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            binding.btnSave.visibility = originalVisibility
            null
        }
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}