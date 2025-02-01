package com.example.mission.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.File

object RotateBitmap {

    /**
     * 파일 경로 [path]를 받아서 Bitmap으로 디코딩하고,
     * EXIF Orientation에 따라 회전 보정한 결과를 반환.
     */
    fun rotateBitmapIfNeeded(path: String): Bitmap? {
        val file = File(path)
        if (!file.exists()) return null

        val originalBitmap = BitmapFactory.decodeFile(path) ?: return null

        // EXIF Orientation 정보 가져오기
        val exif = ExifInterface(path)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        // 사진 각도 이상할시 조정
        val rotationDegrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 270f
            ExifInterface.ORIENTATION_ROTATE_180 -> 90f
            ExifInterface.ORIENTATION_ROTATE_270 -> 90f
            else -> 0f
        }

        if (rotationDegrees == 0f) {
            return originalBitmap
        }

        val matrix = Matrix().apply {
            postRotate(rotationDegrees)
        }

        val rotatedBitmap = Bitmap.createBitmap(
            originalBitmap,
            0,
            0,
            originalBitmap.width,
            originalBitmap.height,
            matrix,
            true
        )
        originalBitmap.recycle()

        return rotatedBitmap
    }
}
