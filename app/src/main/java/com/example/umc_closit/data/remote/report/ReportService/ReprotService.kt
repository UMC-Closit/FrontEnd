package com.example.umc_closit.data.remote.report

import com.example.umc_closit.data.remote.BaseResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class ReportRequest(val content: String)

interface ReportService {
    @POST("/api/v1/report")
    fun report(@Body body: ReportRequest): Call<BaseResponse<String>>
}