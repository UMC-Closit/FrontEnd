package com.example.umc_closit.data.entities

data class Post (
    val title: String, //글 제목
    //val imageUrl: String // 게시글 위치
    val imageResId: Int // 시연시 이미지 리소스 id 사용
)