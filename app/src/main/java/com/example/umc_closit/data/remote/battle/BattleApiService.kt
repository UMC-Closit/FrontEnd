package com.example.umc_closit.data.remote.battle

import CommentPostResult
import CommentRequest
import CommentResult
import com.example.umc_closit.data.BattlePostRequest
import com.example.umc_closit.data.BattlePostResponse
import com.example.umc_closit.data.remote.BaseResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BattleApiService {
    // 배틀 업로드 API
    @Headers("Content-Type: application/json")
    @POST("/api/v1/communities/battle/upload")
    fun uploadBattle(
        @Body request: BattlePostRequest
    ): Call<BattlePostResponse>

    // 배틀 vote API
    @Headers("Content-Type: application/json")
    @POST("/api/v1/communities/battle/{battle_id}/voting")
    fun voteBattle(
        @Path("battle_id") battleId: Long,
        @Body requestBody: Map<String, Int>  // {"postId": value}
    ): Call<VoteResponse>

    // 배틀 도전 API
    @POST("/api/v1/communities/battle/{battle_id}/challenge/upload")
    fun challengeBattle(
        @Path("battle_id") battleId: Long,
        @Body request: BattleChallengeRequest
    ): Call<BattleChallengeResponse>

    // 배틀 게시글 목록 조회 API
    @GET("/api/v1/communities/battle")
    fun getBattleList(
        @Query("page") page: Int,
        @Query("sorting") sorting: String = "LATEST", // "LATEST" or "TRENDING"
        @Query("status") status: String = "ACTIVE"   // "INACTIVE", "PENDING", "ACTIVE", "COMPLETED"
    ): Call<BattleListResponse>

    // 배틀 챌린지 게시글 목록 조회 API
    @GET("/api/v1/communities/battle/challenge")
    fun getChallengeBattles(
        @Query("page") page: Int
    ): Call<ChallengeBattleResponse>

    // 배틀 게시글 삭제 API
    @DELETE("/api/v1/communities/battle/{battle_id}")
    fun deleteBattle(
        @Path("battle_id") battleId: Long
    ): Call<DeleteBattleResponse>

    // 배틀 like API
    @POST("/api/v1/communities/battle/like/{battleId}")
    fun addBattleLike(@Path("battleId") battleId: Long): Call<LikeResponse>

    // 배틀 like 취소 API
    @DELETE("/api/v1/communities/battle/like/{battleLikeId}")
    fun removeBattleLike(@Path("battleLikeId") battleLikeId: Int): Call<LikeResponse>

    // 배틀 댓글 조회 API
    @GET("/api/v1/communities/battle/{battle_id}/comments")
    fun getBattleComments(
        @Path("battle_id") battleId: Long,
        @Query("page") page: Int
    ): Call<BaseResponse<CommentResult>>

    // 배틀 댓글 작성 API
    @POST("/api/v1/communities/battle/{battle_id}/comments")
    fun postBattleComment(
        @Path("battle_id") battleId: Long,
        @Body commentRequest: CommentRequest
    ): Call<BaseResponse<CommentPostResult>>

    // 배틀 댓글 삭제 API
    @DELETE("/api/v1/communities/battle/{battle_id}/comments/{battle_comment_id}")
    fun deleteBattleComment(
        @Path("battle_id") battleId: Long,
        @Path("battle_comment_id") battleCommentId: Int
    ): Call<BaseResponse<String>>

}
