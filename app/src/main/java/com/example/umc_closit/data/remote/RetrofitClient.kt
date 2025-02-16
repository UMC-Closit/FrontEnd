package com.example.umc_closit.data.remote

import android.content.Context
import com.example.umc_closit.data.TodayClosetApiService
import com.example.umc_closit.data.remote.auth.AuthService
import com.example.umc_closit.data.remote.profile.ProfileService
import com.example.umc_closit.data.remote.timeline.TimelineService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://54.180.132.28:8080/"

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val timelineService: TimelineService by lazy {
        retrofit.create(TimelineService::class.java)
    }

    val profileService: ProfileService by lazy {
        retrofit.create(ProfileService::class.java)
    }

    val battleApiService: BattleApiService by lazy {
        retrofit.create(BattleApiService::class.java)
    }
    val todayClosetApiService: TodayClosetApiService by lazy {
        retrofit.create(TodayClosetApiService::class.java)
    }
}
