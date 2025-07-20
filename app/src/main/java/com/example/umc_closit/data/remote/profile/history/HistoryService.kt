package com.example.umc_closit.data.remote.profile.history

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HistoryService {
    @GET("/api/v1/history")
    fun getDateHistoryList(@Query("page") page: Int): Call<DateHistoryResponse>

    @GET("/api/v1/history/pointcolor")
    fun getPointColorHistoryList(@Query("page") page: Int): Call<ColorHistoryResponse>

    @GET("/api/v1/history/detail")
    fun getDetailHistory(@Query("localDate") date: String): Call<DetailHistoryResponse>
}
