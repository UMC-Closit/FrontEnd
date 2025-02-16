package com.example.umc_closit.ui.mission

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded
import com.example.umc_closit.databinding.ActivityPreviewBinding

class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding

    private var mainPhotoPath: String? = null
    private var smallPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🚀 View Binding 초기화
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val frontPhotoPath = intent.getStringExtra("frontPhotoPath")


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        mainPhotoPath = frontPhotoPath


        loadImages()

        /*
        binding.imageViewSmall.setOnClickListener {
            swapImages()
        }

         */

        binding.btnRetake.setOnClickListener {
            val intent = Intent(this, MissionActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, MissionBackActivity::class.java).apply {
                putExtra("frontPhotoPath", frontPhotoPath)

            }
            startActivity(intent)
        }
    }

    private fun loadImages() {
        mainPhotoPath?.let { path ->
            val bitmap = rotateBitmapIfNeeded(path)
            binding.imageViewMain.setImageBitmap(bitmap)
        }
    /*
        smallPhotoPath?.let { path ->
            val bitmap = rotateBitmapIfNeeded(path)
            binding.imageViewSmall.setImageBitmap(bitmap)
        }

    */
    }

    /*
    private fun swapImages() {
        val temp = mainPhotoPath
        mainPhotoPath = smallPhotoPath
        smallPhotoPath = temp

        loadImages()
    }

     */
}