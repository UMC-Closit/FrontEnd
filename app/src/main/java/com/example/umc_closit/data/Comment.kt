package com.example.umc_closit.data

import java.io.Serializable

data class Comment(
    val commentId: Int,
    var userInfo: UserInfo?,           // 사용자 정보 (작성자 정보, 프로필 이미지 등)
    var body: String,                  // 댓글 본문
    var likeCount: Int = 0,            // 좋아요 개수
    val parentCommentId: Int?,         // 상위 댓글 ID (답글 기능)
    var userLike: Boolean,             // 사용자 좋아요 상태
    var createTime: String,            // 댓글 생성 시간
    var updateTime: String,            // 댓글 수정 시간
    var userName: String,              // 사용자 이름
    var commentText: String            // 댓글 텍스트
) : Serializable