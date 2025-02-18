package com.example.umc_closit.data.remote

import com.example.umc_closit.data.BattlePostRequest
import com.example.umc_closit.data.BattlePostResponse
import com.example.umc_closit.data.remote.battle.LikeResponse
import com.example.umc_closit.data.remote.battle.VoteResponse

import retrofit2.Call
import retrofit2.http.*

interface BattleApiService {
    // 배틀 업로드 API
    fun uploadBattle(
        @Body request: BattlePostRequest
    ): Call<BattlePostResponse>


    // 배틀 vote API
    @Headers("Content-Type: application/json")
    @POST("/api/auth/communities/battle/vote")
    fun voteBattle(
        @Body requestBody: Map<String, Long>
    ): Call<VoteResponse>


    // 배틀 like API
    @POST("/api/battle/like/{battleId}")
    fun addBattleLike(@Path("battleId") battleId: Long): Call<LikeResponse>

    // 배틀 like 취소 API
    @DELETE("/api/battle/like/{battleLikeId}")
    fun removeBattleLike(@Path("battleLikeId") battleLikeId: Long): Call<LikeResponse>

}
