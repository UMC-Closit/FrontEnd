package com.example.mission.ui.mission

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mission.R
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded

class PreviewActivity : AppCompatActivity() {

    private lateinit var imageViewMain: ImageView
    private lateinit var imageViewSmall: ImageView
    private lateinit var btnRetake: ImageButton
    private lateinit var btnNext: ImageButton

    private var mainPhotoPath: String? = null
    private var smallPhotoPath: String? = null

    private lateinit var ivLeftButton: ImageView
    private lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        imageViewMain = findViewById(R.id.imageViewMain)
        imageViewSmall = findViewById(R.id.imageViewSmall)
        btnRetake = findViewById(R.id.btnRetake)
        btnNext = findViewById(R.id.btnNext)

        val frontPhotoPath = intent.getStringExtra("frontPhotoPath")
        val backPhotoPath = intent.getStringExtra("backPhotoPath")

        // 상단 툴바
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topNavigationBar)
        setSupportActionBar(toolbar)
        ivLeftButton = toolbar.findViewById(R.id.ivLeftButton)
        tvTitle = toolbar.findViewById(R.id.tvTitle)
        ivLeftButton.setOnClickListener {
            // TODO: 툴바 화살표 버튼 동작 추가
            finish()
        }

        mainPhotoPath = frontPhotoPath
        smallPhotoPath = backPhotoPath

        loadImages()

        imageViewSmall.setOnClickListener {
            swapImages()
        }

        btnRetake.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnNext.setOnClickListener {
            val intent = Intent(this, FrontOnlyActivity::class.java).apply {
                putExtra("frontPhotoPath", frontPhotoPath)
                putExtra("backPhotoPath", backPhotoPath)
            }
            startActivity(intent)
        }
    }

    private fun loadImages() {
        mainPhotoPath?.let { path ->
            val bitmap = rotateBitmapIfNeeded(path)  // <- 여기서 utils 호출
            imageViewMain.setImageBitmap(bitmap)
        }

        smallPhotoPath?.let { path ->
            val bitmap = rotateBitmapIfNeeded(path)
            imageViewSmall.setImageBitmap(bitmap)
        }
    }

    private fun swapImages() {
        val temp = mainPhotoPath
        mainPhotoPath = smallPhotoPath
        smallPhotoPath = temp

        loadImages()
    }
}
