// BattleApiService.kt
package com.example.umc_closit.data.remote

import com.example.umc_closit.data.BattlePostRequest
import com.example.umc_closit.data.BattlePostResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface BattleApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/auth/communities/battle/upload")
    fun uploadBattle(
        @Body request: BattlePostRequest
    ): Call<BattlePostResponse>
}
