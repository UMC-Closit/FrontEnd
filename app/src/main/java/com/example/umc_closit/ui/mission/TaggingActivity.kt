package com.example.mission.ui.mission

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mission.R
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded
import java.io.File
import java.io.FileOutputStream

class TaggingActivity : AppCompatActivity() {

    private lateinit var imageViewTag: ImageView
    private lateinit var btnSave: ImageButton
    private lateinit var taggingLayout: RelativeLayout
    private var originalBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tagging)

        imageViewTag = findViewById(R.id.imageViewTag)
        btnSave = findViewById(R.id.btnSave)
        taggingLayout = findViewById(R.id.taggingLayout)

        val photoPath = intent.getStringExtra("photoPath")
        if (photoPath != null) {
            originalBitmap = rotateBitmapIfNeeded(photoPath)
            originalBitmap?.let { bmp ->
                imageViewTag.setImageBitmap(bmp)
            }
        }

        imageViewTag.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val localX = event.x
                val localY = event.y
                showTagDialog { tagText ->
                    addTagView(tagText, localX, localY)
                }
            }
            true
        }

        btnSave.setOnClickListener {
            val savedFile = captureAndSaveTaggedImage()
            if (savedFile != null) {
                val resultIntent = intent.apply {
                    putExtra("taggedPhotoPath", savedFile.absolutePath)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                // 저장 실패 처리 (추후 추가)
            }
        }
    }

    private fun showTagDialog(onTagSaved: (String) -> Unit) {
        val editText = EditText(this).apply { hint = "해시태그 입력" }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("해시태그 입력")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                val input = editText.text.toString().trim()
                if (input.isNotEmpty()) {
                    onTagSaved(input)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun addTagView(tagText: String, localX: Float, localY: Float) {
        val tagView = TextView(this).apply {
            text = tagText
            setTextColor(Color.WHITE)
            gravity = right
            textSize = 14f
            setBackgroundResource(R.drawable.bg_hashtag)
            val leftPad = dpToPx(20)
            val pad = dpToPx(8)
            setPadding(leftPad, pad, pad, pad)
        }

        val imageLoc = IntArray(2)
        imageViewTag.getLocationOnScreen(imageLoc)

        val parentLoc = IntArray(2)
        taggingLayout.getLocationOnScreen(parentLoc)

        val absoluteX = imageLoc[0] + localX
        val absoluteY = imageLoc[1] + localY

        val finalX = absoluteX - parentLoc[0]
        val finalY = absoluteY - parentLoc[1]

        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = finalX.toInt()
        params.topMargin = finalY.toInt()

        taggingLayout.addView(tagView, params)
        tagView.bringToFront()
    }

    private fun captureAndSaveTaggedImage(): File? {

        val originalVisibility = btnSave.visibility
        btnSave.visibility = View.GONE

        val width = taggingLayout.width
        val height = taggingLayout.height
        if (width <= 0 || height <= 0) {
            btnSave.visibility = originalVisibility
            return null
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        taggingLayout.draw(canvas)

        // 사진 저장 (JPEG, 품질 90)
        val file = File(filesDir, "tagged_${System.currentTimeMillis()}.jpg")
        return try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            btnSave.visibility = originalVisibility
            null
        }

        btnSave.visibility = originalVisibility
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}
