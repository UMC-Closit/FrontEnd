package com.example.umc_closit.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object FileUtils {
    fun createImagePart(partName: String, filePath: String): MultipartBody.Part {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("파일 경로가 잘못되었습니다: $filePath")
        }
        val requestFile = RequestBody.create("image/*".toMediaType(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}