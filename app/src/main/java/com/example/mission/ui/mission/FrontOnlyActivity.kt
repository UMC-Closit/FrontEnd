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
import androidx.appcompat.app.AlertDialog
import com.example.mission.R
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded

class FrontOnlyActivity : AppCompatActivity() {

    private var frontPhotoPath: String? = null
    private var backPhotoPath: String? = null
    private var originalBitmapPath: String? = null

    private lateinit var relativeLayout: RelativeLayout
    private lateinit var imageViewFrontOnly: ImageView
    private lateinit var viewColorIcon: View

    private lateinit var btnHashtag: ImageButton
    private lateinit var textHashtag: TextView

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
        textHashtag = findViewById(R.id.textHashtag)

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


        // 해시태그 버튼 (처음 입력)
        btnHashtag.setOnClickListener {
            showHashtagDialog(
                currentHashtag = null,
                onHashtagSaved = { newHashtag ->
                    // 입력 완료시 버튼을 숨긴 후 텍스트뷰 노출
                    btnHashtag.visibility = View.GONE
                    textHashtag.visibility = View.VISIBLE
                    textHashtag.text = newHashtag
                }
            )
        }

        // 해시태그 수정
        textHashtag.setOnClickListener {
            showHashtagDialog(
                currentHashtag = textHashtag.text.toString(),
                onHashtagSaved = { updatedHashtag ->
                    textHashtag.text = updatedHashtag
                }
            )
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


    // 해시태그 입력 다이얼로그
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
