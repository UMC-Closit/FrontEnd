package com.example.mission.ui.mission

import android.content.Intent
import android.os.Bundle
import android.view.SurfaceView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mission.R
import com.example.mission.camera.CameraPreviewCallback
import com.example.mission.camera.CameraPreviewManager

class MainActivity : AppCompatActivity(), CameraPreviewCallback {

    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceViewSecond: SurfaceView
    private lateinit var captureButton: Button

    private lateinit var cameraPreviewManager: CameraPreviewManager

    private lateinit var ivLeftButton: ImageView
    private lateinit var tvTitle: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 상단 툴바
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topNavigationBar)
        setSupportActionBar(toolbar)
        ivLeftButton = toolbar.findViewById(R.id.ivLeftButton)
        tvTitle = toolbar.findViewById(R.id.tvTitle)
        ivLeftButton.setOnClickListener {
            // TODO: 툴바 화살표 버튼 동작 추가
            finish()
        }

        surfaceView = findViewById(R.id.surfaceView)
        surfaceViewSecond = findViewById(R.id.surfaceViewSecond)
        captureButton = findViewById(R.id.btnCapturePhoto)

        cameraPreviewManager = CameraPreviewManager(
            context = this,
            surfaceView = surfaceView,
            surfaceViewSecond = surfaceViewSecond,
            captureButton = captureButton
        )
        // 콜백 설정
        cameraPreviewManager.callback = this
        cameraPreviewManager.initialize()
    }

    // 두 장이 모두 찍혔을 때 PreviewActivity로 이동
    override fun onBothPhotosCaptured(frontPhotoPath: String, backPhotoPath: String) {
        val intent = Intent(this, PreviewActivity::class.java).apply {
            putExtra("frontPhotoPath", frontPhotoPath)
            putExtra("backPhotoPath", backPhotoPath)
        }
        startActivity(intent)
    }

    // 권한 요청 응답 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraPreviewManager.onRequestPermissionsResult(requestCode, grantResults)
    }
}
