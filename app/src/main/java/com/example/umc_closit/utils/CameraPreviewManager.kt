package com.example.mission.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

/**
 * 카메라 촬영 상태를 Activity에 알리기 위한 콜백 인터페이스
 * 전후면 사진 촬영 완료시 onBothPhotosCaptured 호출
 */
interface CameraPreviewCallback {
    fun onBothPhotosCaptured(frontPhotoPath: String, backPhotoPath: String)
}

/**
 * 전후면 카메라 동시 실행 / 사진 촬영 후 파일로 저장하는 로직을 모듈화한 클래스
 * SurfaceView 2개 (메인/서브)와 촬영 버튼을 받아 초기화
 * 두 개의 Surface가 생성된 뒤 카메라 시작
 * 전면/후면 전환 (SurfaceView 터치)
 * 사진 촬영 시 파일 저장
 * 전후면 사진 모두 찍히면 callback?.onBothPhotosCaptured 호출
 */
class CameraPreviewManager(
    private val context: Context,
    private val surfaceView: SurfaceView,
    private val surfaceViewSecond: SurfaceView,
    private val captureButton: ImageButton
) {

    var callback: CameraPreviewCallback? = null

    // 사진 경로
    var frontPhotoPath: String? = null
    var backPhotoPath: String? = null

    // Camera2 관련
    private lateinit var cameraManager: CameraManager
    private var frontCameraId: String? = null
    private var backCameraId: String? = null
    private var currentCameraId: String? = null

    private var cameraDevice: CameraDevice? = null
    private var secondCameraDevice: CameraDevice? = null
    private var previewSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null

    // SurfaceView 준비 상태
    private var surfaceViewReady = false
    private var surfaceViewSecondReady = false

    // 회전값 매핑 (사진이 틀어졌을 경우 해당 부분 조정)
    private val ORIENTATIONS = SparseIntArray().apply {
        put(Surface.ROTATION_0, 90)
        put(Surface.ROTATION_90, 0)
        put(Surface.ROTATION_180, 270)
        put(Surface.ROTATION_270, 180)
    }

    // 카메라 권한 코드
    private val cameraPermissionRequestCode = 101

    // 초기화(CameraManager 획득, SurfaceHolder 콜백 등록, 버튼 리스너 설정)
    fun initialize() {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        // 첫 번째 SurfaceView
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                surfaceViewReady = true
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })

        // 두 번째 SurfaceView
        surfaceViewSecond.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                surfaceViewSecondReady = true
                tryStartCameraIfBothReady()
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })

        // 전면/후면 전환 (SurfaceView 터치)
        surfaceView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (currentCameraId == frontCameraId) {
                    startCamera(backCameraId)
                } else {
                    startCamera(frontCameraId)
                }
            }
            true
        }

        // 촬영 버튼
        captureButton.setOnClickListener {
            takePicture()
        }
    }

    //두 SurfaceView가 모두 준비된 후 시작 (버그 방지)
    private fun tryStartCameraIfBothReady() {
        if (surfaceViewReady && surfaceViewSecondReady) {
            if (hasCameraPermission()) {
                getCameraIds()
                startCamera(frontCameraId)
            } else {
                requestCameraPermission()
            }
        }
    }

    // 카메라 권한 확인
    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 카메라 권한 요청
    private fun requestCameraPermission() {
        if (context is Activity && context is ActivityCompat.OnRequestPermissionsResultCallback) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionRequestCode
            )
        } else {
            Toast.makeText(context, "카메라 권한 요청 실패", Toast.LENGTH_SHORT).show()
        }
    }

    //카메라 ID 확인
    private fun getCameraIds() {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                when (lensFacing) {
                    CameraCharacteristics.LENS_FACING_FRONT -> frontCameraId = cameraId
                    CameraCharacteristics.LENS_FACING_BACK -> backCameraId = cameraId
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // 카메라 열기
    private fun startCamera(cameraId: String?) {
        cameraId ?: return
        try {
            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice?.close()
                    cameraDevice = camera
                    currentCameraId = cameraId

                    startPreview(cameraDevice)

                    // 반대편 카메라 (작게)
                    val secondId = if (currentCameraId == frontCameraId) backCameraId else frontCameraId
                    startSecondCamera(secondId)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // 메인 카메라 프리뷰
    private fun startPreview(device: CameraDevice?) {
        device ?: return
        val previewSurface = surfaceView.holder.surface

        imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 1).apply {
            setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
                val savedFile = saveImage(image.planes[0].buffer)
                image.close()

                // 전면 or 후면 경로
                if (currentCameraId == frontCameraId) {
                    frontPhotoPath = savedFile.absolutePath
                } else {
                    backPhotoPath = savedFile.absolutePath
                }

                // 두 장 다 있으면 콜백
                if (!frontPhotoPath.isNullOrEmpty() && !backPhotoPath.isNullOrEmpty()) {
                    // 여기서 직접 startActivity 대신 callback
                    callback?.onBothPhotosCaptured(frontPhotoPath!!, backPhotoPath!!)
                }
            }, null)
        }

        try {
            device.createCaptureSession(
                listOf(previewSurface, imageReader!!.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        previewSession = session
                        val requestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                        requestBuilder.addTarget(previewSurface)
                        session.setRepeatingRequest(requestBuilder.build(), null, null)
                    }
                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Toast.makeText(context, "Preview config failed", Toast.LENGTH_SHORT).show()
                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // 두 번째 카메라 (작은 화면)
    private fun startSecondCamera(cameraId: String?) {
        cameraId ?: return
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                secondCameraDevice?.close()
                secondCameraDevice = camera

                val smallSurface = surfaceViewSecond.holder.surface
                try {
                    camera.createCaptureSession(
                        listOf(smallSurface),
                        object : CameraCaptureSession.StateCallback() {
                            override fun onConfigured(session: CameraCaptureSession) {
                                val builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                                builder.addTarget(smallSurface)
                                session.setRepeatingRequest(builder.build(), null, null)
                            }
                            override fun onConfigureFailed(session: CameraCaptureSession) {
                                Toast.makeText(context, "2nd camera config failed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        null
                    )
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onDisconnected(camera: CameraDevice) {
                camera.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
            }
        }, null)
    }

    // 촬영 (스틸 캡처)
    fun takePicture() {
        val cam = cameraDevice ?: return
        val session = previewSession ?: return
        val readerSurface = imageReader?.surface ?: return

        val characteristics = cameraManager.getCameraCharacteristics(currentCameraId ?: return)
        val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
        val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
        val rotation = (context as? Activity)?.windowManager?.defaultDisplay?.rotation ?: Surface.ROTATION_0
        val deviceOrientation = ORIENTATIONS.get(rotation)

        val jpegOrientation = if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            (sensorOrientation - deviceOrientation + 270) % 360
        } else {
            (sensorOrientation + deviceOrientation + 360) % 360
        }

        try {
            val captureBuilder = cam.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
                addTarget(readerSurface)
                set(CaptureRequest.JPEG_ORIENTATION, jpegOrientation)
            }

            session.capture(captureBuilder.build(), object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    super.onCaptureCompleted(session, request, result)
                    Toast.makeText(context, "Photo Captured!", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // 이미지 파일로 저장
    private fun saveImage(buffer: ByteBuffer): File {
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val file = File(context.filesDir, "captured_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { fos ->
            fos.write(bytes)
        }
        return file
    }

    // Activity 권한 요청 결과 전달
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == cameraPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCameraIds()
                startCamera(frontCameraId)
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
