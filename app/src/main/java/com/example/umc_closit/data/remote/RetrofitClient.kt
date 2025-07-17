package com.example.umc_closit.data.remote


import ChallengeApiService
import android.content.Context
import android.util.Log
import com.example.umc_closit.data.TodayClosetApiService
import com.example.umc_closit.data.remote.auth.AuthService
import com.example.umc_closit.data.remote.post.PostService
import com.example.umc_closit.data.remote.profile.ProfileService
import com.example.umc_closit.data.remote.timeline.TimelineService
import com.example.umc_closit.data.remote.profile.history.HistoryService
import com.example.umc_closit.data.remote.battle.BattleApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://54.180.132.28:8080/"

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        // 로깅 인터셉터 추가
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("API_RESPONSE", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(loggingInterceptor) // 로깅 인터셉터 추가
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

    val historyService: HistoryService by lazy {
        retrofit.create(HistoryService::class.java)
    }

    val challengeApiService: ChallengeApiService by lazy {
        retrofit.create(ChallengeApiService::class.java)
    }

    val postService: PostService by lazy{
        retrofit.create(PostService::class.java)
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}


