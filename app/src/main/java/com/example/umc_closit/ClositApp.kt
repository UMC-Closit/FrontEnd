package com.example.umc_closit

import android.app.Application
import com.example.umc_closit.data.remote.RetrofitClient

class ClositApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // 앱 시작할 때 Retrofit 초기화
        RetrofitClient.init(this)
    }
}
