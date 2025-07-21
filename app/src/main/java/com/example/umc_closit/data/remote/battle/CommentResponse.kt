data class CommentResult(
    val battleCommentPreviewList: List<BattleComment>,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)

data class BattleComment(
    val battleCommentId: Int,
    val parentBattleCommentId: Long,
    val clositId: String,
    val thumbnail: String,
    val content: String,
    val createdAt: String
)

data class CommentRequest(
    val content: String,
    val parentCommentId: Long? = null
)

data class CommentPostResult(
    val battleCommentId: Int,
    val clositId: String,
    val createdAt: String
)