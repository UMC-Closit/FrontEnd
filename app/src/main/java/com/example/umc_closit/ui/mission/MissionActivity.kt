package com.example.umc_closit.ui.mission

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mission.camera.CameraPreviewCallback
import com.example.mission.camera.CameraPreviewManager
import com.example.umc_closit.databinding.ActivityMissionBinding

class MissionActivity : AppCompatActivity(), CameraPreviewCallback {

    private lateinit var binding: ActivityMissionBinding
    private lateinit var cameraPreviewManager: CameraPreviewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🚀 View Binding 초기화
        binding = ActivityMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼 클릭 리스너 설정
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        // CameraPreviewManager 초기화
        cameraPreviewManager = CameraPreviewManager(
            context = this,
            surfaceView = binding.surfaceView,
            surfaceViewSecond = binding.surfaceViewSecond,
            captureButton = binding.btnCapturePhoto
        )

        // 콜백 설정 및 초기화
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