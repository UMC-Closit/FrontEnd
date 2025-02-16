package com.example.umc_closit.data.remote.battle

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BattleApiService {
    @POST("/api/auth/communities/battle/upload")
    fun uploadBattle(
        @Header("Authorization") token: String,
        @Body request: BattlePostRequest
    ): Call<BattlePostResponse>
}
