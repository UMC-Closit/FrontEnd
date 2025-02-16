package com.example.umc_closit.data.remote

import android.content.Context
import com.example.umc_closit.data.remote.auth.AuthService
import com.example.umc_closit.data.remote.battle.BattleApiService
import com.example.umc_closit.data.remote.profile.ProfileService
import com.example.umc_closit.data.remote.timeline.TimelineService
<<<<<<< Updated upstream
import com.example.umc_closit.utils.TokenUtils
import okhttp3.Interceptor
=======
import com.example.umc_closit.data.remote.post.PostService
>>>>>>> Stashed changes
import okhttp3.OkHttpClient
import okhttp3.Response
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

    val battleService: BattleApiService by lazy {
        retrofit.create(BattleApiService::class.java)
    }
<<<<<<< Updated upstream
=======
    val todayClosetApiService: TodayClosetApiService by lazy {
        retrofit.create(TodayClosetApiService::class.java)
    }
    val postService: PostService by lazy {
        retrofit.create(PostService::class.java)
    }
>>>>>>> Stashed changes
}
