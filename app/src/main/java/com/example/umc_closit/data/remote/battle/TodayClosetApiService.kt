package com.example.umc_closit.data

import com.example.umc_closit.data.remote.battle.TodayClosetResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TodayClosetApiService {
    @GET("/api/auth/communities/todayclosets")
    fun getTodayClosets(
        @Query("page") page: Int
    ): Call<TodayClosetResponse>


}


