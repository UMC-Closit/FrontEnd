package com.example.umc_closit.data.remote

import com.example.umc_closit.data.remote.battle.BattleApiService
import com.example.umc_closit.data.remote.auth.AuthService
import com.example.umc_closit.data.remote.profile.ProfileService
import com.example.umc_closit.data.remote.timeline.TimelineService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://54.180.132.28:8080/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val instance: BattleApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BattleApiService::class.java)
    }

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val timelineService: TimelineService = retrofit.create(TimelineService::class.java)
    val profileService: ProfileService = retrofit.create(ProfileService::class.java)

}