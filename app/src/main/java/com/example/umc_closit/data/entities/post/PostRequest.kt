package com.example.umc_closit.data.entities.post

import kotlinx.serialization.Serializable

@Serializable
data class PostRequest(
    val frontImage: String,
    val backImage: String,
    val hashtags: List<String>,
    val frontItemtags: List<ItemTag>,
    val backItemtags: List<ItemTag>,
    val pointColor: String,
    val visibility: String,
    val mission: Boolean
)

@Serializable
data class ItemTag(
    val x: Int,
    val y: Int,
    val content: String
)

