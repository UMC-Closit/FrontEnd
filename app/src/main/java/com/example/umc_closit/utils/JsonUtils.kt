package com.example.umc_closit.utils

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody

object JsonUtils {
    fun createRequestBody(requestObject: Any): RequestBody {
        val gson = Gson()
        val jsonString = gson.toJson(requestObject)
        return RequestBody.create("application/json; charset=utf-8".toMediaType(), jsonString)
    }
}