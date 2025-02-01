package com.example.mission.ui.mission

import android.widget.*
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.drawable.GradientDrawable
import android.widget.ImageButton
import android.widget.EditText
import android.widget.TextView
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import com.example.mission.R
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers

class FrontOnlyActivity : AppCompatActivity() {

    private var frontPhotoPath: String? = null
    private var backPhotoPath: String? = null
    private var originalBitmapPath: String? = null

    private lateinit var relativeLayout: RelativeLayout
    private lateinit var imageViewFrontOnly: ImageView
    private lateinit var viewColorIcon: View

    private lateinit var btnHashtag: ImageButton
    private lateinit var hashtagContainer: LinearLayout

    private val hashtagsFlow = MutableStateFlow<List<String>>(emptyList())

    private lateinit var btnContinue: ImageButton

    private var originalBitmap: Bitmap? = null

    private lateinit var ivLeftButton: ImageView
    private lateinit var tvTitle: TextView


    companion object {
        private const val TAGGING_REQUEST_CODE = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front_only)

        // 상단 툴바
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topNavigationBar)
        setSupportActionBar(toolbar)
        ivLeftButton = toolbar.findViewById(R.id.ivLeftButton)
        tvTitle = toolbar.findViewById(R.id.tvTitle)
        ivLeftButton.setOnClickListener {
            // TODO: 툴바 화살표 버튼 동작 추가
            finish()
        }

        relativeLayout = findViewById(R.id.relativeLayout)
        imageViewFrontOnly = findViewById(R.id.imageViewFrontOnly)
        viewColorIcon = findViewById(R.id.viewColorIcon)

        btnHashtag = findViewById(R.id.btnHashtag)
        hashtagContainer = findViewById(R.id.hashtagContainer)

        frontPhotoPath = intent.getStringExtra("frontPhotoPath")
        frontPhotoPath?.let { path ->
            originalBitmap = rotateBitmapIfNeeded(path)
            originalBitmap?.let { bmp ->
                imageViewFrontOnly.setImageBitmap(bmp)
            }



        }


        backPhotoPath = intent.getStringExtra("backPhotoPath")

        var isColorExtractMode = false

        viewColorIcon.setOnClickListener {
            isColorExtractMode = !isColorExtractMode
        }


        imageViewFrontOnly.setOnTouchListener { view, event ->
            if (isColorExtractMode) {
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    originalBitmap?.let { bmp ->
                        val color = getTouchedColor(bmp, view as ImageView, event.x, event.y)
                        setIconColor(viewColorIcon, color)
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
        btnHashtag.setOnClickListener {
            showHashtagDialog { newHashtag ->
                addHashtag(newHashtag)
            }
        }

        // BackOnlyActivity로 이동
        btnContinue = findViewById(R.id.btnContinue)
        btnContinue.setOnClickListener {
            val intent = Intent(this, BackOnlyActivity::class.java).apply {
                putExtra("backPhotoPath", backPhotoPath)
            }
            startActivity(intent)
        }
    }

    private fun addHashtag(hashtag: String) {
        // Update the flow with the new hashtag
        val currentHashtags = hashtagsFlow.value.toMutableList()
        currentHashtags.add(hashtag)
        hashtagsFlow.value = currentHashtags
    }

    // 해시태그 입력 다이얼로그
    private fun showHashtagDialog(onHashtagSaved: (String) -> Unit) {
        val editText = EditText(this).apply {
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
        hashtagContainer.removeAllViews()

        hashtags.forEach { hashtag ->
            val hashtagTextView = TextView(this).apply {
                text = "#$hashtag"
                setTextColor(resources.getColor(R.color.white))
                setPadding(16, 8, 16, 8)
                background = resources.getDrawable(R.drawable.bg_detail_hashtag, null)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 8
                }
            }

            // Add the hashtag to the container
            hashtagContainer.addView(hashtagTextView)
        }
    }

    // 아이콘 색상 변경
    private fun setIconColor(view: View, color: Int) {
        val bg = view.background
        if (bg is GradientDrawable) {
            bg.setColor(color)
        } else {
            view.setBackgroundColor(color)
        }
    }

    // 이미지에서 색상 추출
    private fun getTouchedColor(
        bitmap: Bitmap,
        imageView: ImageView,
        touchX: Float,
        touchY: Float
    ): Int {
        val ivWidth = imageView.width
        val ivHeight = imageView.height

        val bmpWidth = bitmap.width
        val bmpHeight = bitmap.height

        val xRatio = touchX / ivWidth
        val yRatio = touchY / ivHeight

        val pixelX = (xRatio * bmpWidth).toInt().coerceIn(0, bmpWidth - 1)
        val pixelY = (yRatio * bmpHeight).toInt().coerceIn(0, bmpHeight - 1)

        return bitmap.getPixel(pixelX, pixelY)
    }

    // DP -> PX 변환
    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAGGING_REQUEST_CODE && resultCode == RESULT_OK) {
            val taggedPhotoPath = data?.getStringExtra("taggedPhotoPath")
            if (!taggedPhotoPath.isNullOrEmpty()) {
                originalBitmapPath = taggedPhotoPath
                val bmp = BitmapFactory.decodeFile(taggedPhotoPath)
                if (bmp != null) {
                    imageViewFrontOnly.setImageBitmap(bmp)
                }
            }
        }
    }
}
