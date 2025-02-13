// RetrofitClient.kt
package com.example.umc_closit.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://13.209.5.217:8080/swagger-ui/index.html" // API 서버 주소

    val instance: BattleApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BattleApiService::class.java)
    }
}
