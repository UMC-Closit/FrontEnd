package com.example.umc_closit.ui.timeline.detail

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.post.ItemTag
import com.example.umc_closit.data.remote.post.PostDetail
import com.example.umc_closit.data.remote.timeline.BookmarkRequest
import com.example.umc_closit.databinding.ActivityDetailBinding
import com.example.umc_closit.ui.profile.ProfileFragment
import com.example.umc_closit.ui.timeline.TimelineActivity
import com.example.umc_closit.ui.timeline.comment.CommentBottomSheetFragment
import com.example.umc_closit.utils.FileUtils
import com.example.umc_closit.utils.HashtagUtils
import com.example.umc_closit.utils.TokenUtils

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var postId: Int = -1
    private var position: Int = -1
    private var isFrontImageBig = true
    private var isTagVisible = false
    private var isLiked = false // 좋아요 상태 변수
    private var isSaved = false // 북마크 상태 변수

    private lateinit var post: PostDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getIntExtra("postId", -1)
        position = intent.getIntExtra("position", -1)

        fetchPostDetail(postId)
        Log.d("POST","receive postId: ${postId}")

        // 댓글 동기화 및 UI 업데이트
        updateLikeAndBookmark()

        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.ivImageBig.setOnClickListener {
            toggleTags()
        }

        binding.ivImageSmall.setOnClickListener {
            swapImagesWithTags()
        }

        binding.ivComment.setOnClickListener {
            val commentFragment = CommentBottomSheetFragment.newInstance(postId)
            commentFragment.show(supportFragmentManager, commentFragment.tag)
        }

// 디테일 액티비티에서 프로필 이미지를 클릭했을 때
        binding.ivUserProfile.setOnClickListener {
            // 타임라인 액티비티로 이동
            val intent = Intent(this, TimelineActivity::class.java)
            intent.putExtra("profileUserClositId", post.clositId) // profileUserClositId를 넘겨준다
            startActivity(intent)
        }


        binding.ivLike.setOnClickListener {
            if (isLiked) {
                removeLike(postId)
            } else {
                addLike(postId)
            }
        }

        binding.ivSave.setOnClickListener {
            if (isSaved) {
                removeBookmark(postId)
            } else {
                addBookmark(postId)
            }
        }
    }

    private fun updateLikeAndBookmark() {
        // 좋아요/북마크 상태 업데이트
        binding.ivLike.setImageResource(if (isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off)
        binding.ivSave.setImageResource(if (isSaved) R.drawable.ic_save_on else R.drawable.ic_save_off)
    }

    private fun fetchPostDetail(postId: Int) {
        val apiCall = { RetrofitClient.postService.getPostDetail(postId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    post = response.result
                    bindPostDetail(post)
                    isLiked = post.isLiked
                    isSaved = post.isSaved
                    Log.d("POST","$isLiked, $isSaved")
                    updateLikeAndBookmark()
                } else {
                    Toast.makeText(this, "게시글 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }

    private fun bindPostDetail(post: PostDetail) {
        // 이미지 및 데이터 바인딩
        Glide.with(this).load(post.frontImage).into(binding.ivImageBig)
        Glide.with(this).load(post.backImage).into(binding.ivImageSmall)

        // 프로필 이미지 업데이트
        Glide.with(this).load(post.profileImage).circleCrop().into(binding.ivUserProfile)

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(post.pointColor))
            setStroke(2, Color.BLACK)
        }
        binding.viewColorIcon.background = drawable

        HashtagUtils.displayHashtags(
            context = this,
            hashtags = post.hashtags,
            flow = binding.flowHashtagContainer,
            parentLayout = binding.clHashtag
        )
    }

    private fun toggleTags() {
        val tags = if (isFrontImageBig) post.frontItemtags else post.backItemtags

        if (isTagVisible) {
            binding.clTagContainer.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    binding.clTagContainer.removeAllViews()
                    isTagVisible = false
                }
                .start()
        } else {
            binding.clTagContainer.removeAllViews()

            for (tag in tags) {
                FileUtils.addItemTagView(
                    context = this,
                    container = binding.clTagContainer,
                    imageView = binding.ivImageBig,
                    tag = tag
                )
            }

            binding.clTagContainer.alpha = 0f
            binding.clTagContainer.animate()
                .alpha(1f)
                .setDuration(200)
                .start()

            isTagVisible = true
        }
    }

    private fun swapImagesWithTags() {
        FileUtils.swapImagesWithTagEffect(
            bigImageView = binding.ivImageBig,
            smallImageView = binding.ivImageSmall,
            tagContainer = binding.clTagContainer
        ) {
            isFrontImageBig = !isFrontImageBig
            isTagVisible = false
            binding.clTagContainer.alpha = 0f
        }
    }

    private fun addLike(postId: Int) {
        val apiCall = { RetrofitClient.timelineService.addLike(postId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    isLiked = true
                    updateLikeAndBookmark()
                } else {
                    Toast.makeText(this, "좋아요 추가 실패", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }

    private fun removeLike(postId: Int) {
        val apiCall = { RetrofitClient.timelineService.removeLike(postId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    isLiked = false
                    updateLikeAndBookmark()
                } else {
                    Toast.makeText(this, "좋아요 취소 실패", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }

    private fun addBookmark(postId: Int) {
        val apiCall = { RetrofitClient.timelineService.addBookmark(BookmarkRequest(postId)) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    isSaved = true
                    updateLikeAndBookmark()
                } else {
                    Toast.makeText(this, "북마크 추가 실패", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }

    private fun removeBookmark(postId: Int) {
        val apiCall = { RetrofitClient.timelineService.removeBookmark(postId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    isSaved = false
                    updateLikeAndBookmark()
                } else {
                    Toast.makeText(this, "북마크 취소 실패", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("isLiked", isLiked)
        intent.putExtra("isSaved", isSaved)
        intent.putExtra("position", position)
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }
}
