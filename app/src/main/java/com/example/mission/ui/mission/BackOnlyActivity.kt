package com.example.mission.ui.mission

import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import com.example.mission.R

class BackOnlyActivity : AppCompatActivity() {

    private lateinit var imageViewBackOnly: ImageView
    private lateinit var viewColorIcon: View
    private var originalBitmapPath: String? = null

    private lateinit var btnHashtag: ImageButton
    private lateinit var textHashtag: TextView
    private lateinit var btnPrivacy: ImageButton
    // 업로드 버튼 (추후 연결 필요)
    private lateinit var btnUpload: ImageButton

    private var originalBitmap: Bitmap? = null

    private lateinit var ivLeftButton: ImageView
    private lateinit var tvTitle: TextView

    companion object {
        private const val TAGGING_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_only)

        // 상단 툴바
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topNavigationBar)
        setSupportActionBar(toolbar)
        ivLeftButton = toolbar.findViewById(R.id.ivLeftButton)
        tvTitle = toolbar.findViewById(R.id.tvTitle)
        ivLeftButton.setOnClickListener {
            // TODO: 툴바 화살표 버튼 동작 추가
            finish()
        }

        imageViewBackOnly = findViewById(R.id.imageViewBackOnly)
        viewColorIcon = findViewById(R.id.viewColorIcon)

        btnHashtag = findViewById(R.id.btnHashtag)
        textHashtag = findViewById(R.id.textHashtag)
        btnPrivacy = findViewById(R.id.btnPrivacy)

        btnUpload = findViewById(R.id.btnUpload)

        val backPhotoPath = intent.getStringExtra("backPhotoPath")
        backPhotoPath?.let { path ->
            originalBitmap = rotateBitmapIfNeeded(path)
            originalBitmap?.let { bmp ->
                imageViewBackOnly.setImageBitmap(bmp)
            }
        }

        var isColorExtractMode = false

        viewColorIcon.setOnClickListener {
            isColorExtractMode = !isColorExtractMode
        }

        imageViewBackOnly.setOnTouchListener { view, event ->
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
                        putExtra("photoPath", backPhotoPath)
                    }
                    startActivityForResult(intent, TAGGING_REQUEST_CODE)
                }
            }
                true
        }

        btnHashtag.setOnClickListener {
            showHashtagDialog(
                currentHashtag = null,
                onHashtagSaved = { newHashtag ->
                    btnHashtag.visibility = View.GONE
                    textHashtag.visibility = View.VISIBLE
                    textHashtag.text = newHashtag
                }
            )
        }

        textHashtag.setOnClickListener {
            showHashtagDialog(
                currentHashtag = textHashtag.text.toString(),
                onHashtagSaved = { updatedHashtag ->
                    textHashtag.text = updatedHashtag
                }
            )
        }

        /**
         * 공개범위 버튼과 업로드 버튼 추후 연결
        */
        // 공개범위 버튼 (추후 연결)
        // btnPrivacy.setOnClickListener { }
        // 업로드 버튼 (추후 연결)
        // btnUpload.setOnClickListener { }
    }

    private fun showHashtagDialog(
        currentHashtag: String?,
        onHashtagSaved: (String) -> Unit
    ) {
        val editText = EditText(this).apply {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAGGING_REQUEST_CODE && resultCode == RESULT_OK) {
            val taggedPhotoPath = data?.getStringExtra("taggedPhotoPath")
            if (!taggedPhotoPath.isNullOrEmpty()) {
                originalBitmapPath = taggedPhotoPath
                val bmp = BitmapFactory.decodeFile(taggedPhotoPath)
                if (bmp != null) {
                    imageViewBackOnly.setImageBitmap(bmp)
                }
            }
        }
    }
}


