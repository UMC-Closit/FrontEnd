package com.example.umc_closit.data.remote

import com.example.umc_closit.data.BattlePostRequest
import com.example.umc_closit.data.BattlePostResponse
import com.example.umc_closit.data.LikeResponse
import com.example.umc_closit.data.VoteResponse
import retrofit2.Call
import retrofit2.http.*

interface BattleApiService {

    // 배틀 업로드 API
    @Headers("Content-Type: application/json")
    @POST("/api/auth/communities/battle/upload")
    fun uploadBattle(
        @Body request: BattlePostRequest
    ): Call<BattlePostResponse>

    // 배틀 투표 API
    @POST("/api/auth/communities/battle/{battle_id}/voting")
    fun voteBattle(
        @Path("battle_id") battleId: Long,
        @Body requestBody: Map<String, Long>
    ): Call<VoteResponse>

    // 배틀 like API
    @POST("/api/battle/like/{battleId}")
    fun addBattleLike(@Path("battleId") battleId: Long): Call<LikeResponse>
    // 배틀 like 취소 API
    @DELETE("/api/battle/like/{battleLikeId}")
    fun removeBattleLike(@Path("battleLikeId") battleLikeId: Long): Call<LikeResponse>

}
