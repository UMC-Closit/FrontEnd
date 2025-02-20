package com.example.umc_closit.data.remote.battle

import com.example.umc_closit.data.BattlePostRequest
import com.example.umc_closit.data.BattlePostResponse
import com.example.umc_closit.data.VoteRequest
import com.example.umc_closit.data.remote.battle.LikeResponse
import com.example.umc_closit.data.remote.battle.VoteResponse
import com.example.umc_closit.data.remote.battle.CommentResponse

import retrofit2.Call
import retrofit2.http.*

interface BattleApiService {
    // 배틀 업로드 API
    @Headers("Content-Type: application/json")
    @POST("/api/auth/communities/battle/upload")
    fun uploadBattle(
        @Body request: BattlePostRequest
    ): Call<BattlePostResponse>

    // 배틀 vote API
    @Headers("Content-Type: application/json")
    @POST("/api/auth/communities/battle/vote")
    fun voteBattle(
        @Path("battle_id") battleId: Int,
        @Body requestBody: Map<String, Int>  // {"postId": value}
    ): Call<VoteResponse>

    // 배틀 도전 API
    @Headers("Content-Type: application/json")
    @POST("/api/auth/communities/battle/challenge/upload/{battle_id}")
    fun challengeBattle(
        @Path("battle_id") battleId: Int,
        @Body request: BattleChallengeRequest
    ): Call<BattleChallengeResponse>

    // 배틀 게시글 목록 조회 API
    @GET("/api/auth/communities/battle")
    fun getBattleList(
        @Query("page") page: Int
    ): Call<BattleListResponse>

    // 배틀 챌린지 게시글 목록 조회 API
    @GET("/api/auth/communities/battle/challenge")
    fun getChallengeBattles(
        @Query("page") page: Int
    ): Call<ChallengeBattleResponse>

    // 배틀 게시글 삭제 API
    @DELETE("/api/auth/communities/battle/{battle_id}")
    fun deleteBattle(
        @Path("battle_id") battleId: Long
    ): Call<DeleteBattleResponse>

    // 배틀 like API
    @POST("/api/battle/like/{battleId}")
    fun addBattleLike(@Path("battleId") battleId: Int): Call<LikeResponse>

    // 배틀 like 취소 API
    @DELETE("/api/battle/like/{battleLikeId}")
    fun removeBattleLike(@Path("battleLikeId") battleLikeId: Int): Call<LikeResponse>

    // 배틀 댓글 조회 API
    @GET("/api/auth/communities/battle/{battle_id}/comments")
    fun getBattleComments(
        @Path("battle_id") battleId: Int,
        @Query("page") page: Int
    ): Call<CommentResponse>

    // 배틀 댓글 작성 API
    @POST("/api/auth/communities/battle/{battle_id}/comments")
    fun postBattleComment(
        @Path("battle_id") battleId: Int,
        @Body commentRequest: CommentRequest
    ): Call<CommentPostResponse>

    // 배틀 댓글 삭제 API
    @DELETE("/api/auth/communities/battle/{battle_id}/comments/{battle_comment_id}")
    fun deleteBattleComment(
        @Path("battle_id") battleId: Int,
        @Path("battle_comment_id") battleCommentId: Int
    ): Call<DeleteCommentResponse>

}
