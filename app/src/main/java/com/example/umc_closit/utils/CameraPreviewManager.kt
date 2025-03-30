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
interface CameraFrontCallback {

    fun onFrontPhotoCaptured(frontPhotoPath: String)
}

interface CameraBackCallback{
    fun onBothPhotosCaptured(backPhotoPath: String)
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
    private val captureButton: ImageButton,
    private val cameraType: CameraType // 전면 / 후면 카메라를 구분
) {

    enum class CameraType {
        FRONT, BACK
    }

    var frontCallback: CameraFrontCallback? = null
    var backCallback: CameraBackCallback? = null

    // 사진 경로
    var frontPhotoPath: String? = null
    var backPhotoPath: String? = null

    private lateinit var cameraManager: CameraManager
    private var frontCameraId: String? = null
    private var backCameraId: String? = null
    private var currentCameraId: String? = null

    private var cameraDevice: CameraDevice? = null
    private var previewSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null

    private var surfaceViewReady = false

    private val ORIENTATIONS = SparseIntArray().apply {
        put(Surface.ROTATION_0, 90)
        put(Surface.ROTATION_90, 0)
        put(Surface.ROTATION_180, 270)
        put(Surface.ROTATION_270, 180)
    }

    private val cameraPermissionRequestCode = 101

    fun initialize() {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                surfaceViewReady = true
                tryStartCameraIfReady()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                closeCamera()
            }
        })

        captureButton.setOnClickListener {
            takePicture()
        }
    }

    private fun tryStartCameraIfReady() {
        if (surfaceViewReady) {
            if (hasCameraPermission()) {
                getCameraIds()
                startCamera(cameraType) // 카메라 타입을 인자로 넘겨 전면/후면 설정
            } else {
                requestCameraPermission()
            }
        }
    }

    private fun startCamera(cameraType: CameraType) {
        val cameraId = when (cameraType) {
            CameraType.FRONT -> frontCameraId
            CameraType.BACK -> backCameraId
        } ?: return

        try {
            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    synchronized(this@CameraPreviewManager) {
                        cameraDevice?.close()
                        cameraDevice = camera
                        currentCameraId = cameraId
                        startPreview(cameraDevice)
                    }
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                    cameraDevice = null
                    // 카메라가 끊어진 경우 재연결 시도
                    startCamera(cameraType)
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    cameraDevice = null
                    Toast.makeText(context, "카메라 오류 발생: $error", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() {
        previewSession?.close()
        previewSession = null
        cameraDevice?.close()
        cameraDevice = null
        imageReader?.close()
        imageReader = null
    }

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

    private fun startPreview(device: CameraDevice?) {
        device ?: return
        val previewSurface = surfaceView.holder.surface

        imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 1).apply {
            setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
                val savedFile = saveImage(image.planes[0].buffer)
                image.close()

                // 전면 / 후면 경로 처리
                if (currentCameraId == frontCameraId) {
                    frontPhotoPath = savedFile.absolutePath
                    //전면 사진 촬영 시 콜백 호출
                    if (!frontPhotoPath.isNullOrEmpty()) {
                        frontCallback?.onFrontPhotoCaptured(frontPhotoPath!!)
                    }

                } else {
                    backPhotoPath = savedFile.absolutePath
                    // 사진이 두 개 다 찍혔을 때 콜백 호출
                    if ( !backPhotoPath.isNullOrEmpty()) {
                        backCallback?.onBothPhotosCaptured(backPhotoPath!!)
                    }
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
            (sensorOrientation - deviceOrientation + 90) % 360
        } else {
            (sensorOrientation + deviceOrientation + 270) % 360
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

    private fun saveImage(buffer: ByteBuffer): File {
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val file = File(context.filesDir, "captured_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { fos -> fos.write(bytes) }
        return file
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

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

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == cameraPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCameraIds()
                startCamera(CameraType.FRONT)  // 전면 카메라만 시작
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
