// BattlePostResponse.kt
package com.example.umc_closit.data

data class BattlePostResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: BattleResult?
)

data class BattleResult(
    val battleId: Int,
    val deadline: String,
    val createdAt: String
)
