package com.example.umc_closit.ui.upload

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.battle.TodayClosetItem
import com.example.umc_closit.data.remote.battle.TodayClosetUploadRequest
import com.example.umc_closit.databinding.ActivityUploadBinding
import com.example.umc_closit.ui.timeline.TimelineActivity
import com.example.umc_closit.utils.TokenUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private var todayPosts = mutableListOf<TodayClosetItem>()
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = this.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val profileUserClositId = sharedPreferences.getString("clositId", "") ?: ""


        // 버튼 클릭 시 업로드 액티비티
        binding.btnUpload.setOnClickListener {
            // 업로드 로직
            uploadSelectedPost()
        }

        fetchRecentPosts(profileUserClositId, currentPage)
    }

    // 오늘 날짜의 게시물만 필터링하여 ViewPager에 표시
    private fun fetchRecentPosts(clositId: String, page: Int) {
        val apiCall = { RetrofitClient.postService.getRecentPosts(clositId, page) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                Log.d("UPLOAD", "API Call Success: ${response.isSuccess}")  // API 호출 성공 로그 추가
                if (response.isSuccess) {
                    val posts = response.result.userRecentPostDTOList
                    Log.d("UPLOAD", "Posts fetched: ${posts.size}") // 가져온 게시물 수 확인
                    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    todayPosts.addAll(posts.filter { post ->
                        try {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val postDate = post.createdAt.replace("/", "-")
                            val parsedDate = dateFormat.parse(postDate)
                            val postDateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDate)
                            postDateFormatted == todayDate
                        } catch (e: Exception) {
                            Log.e("UPLOAD", "Date parsing error: ${e.message}")
                            false
                        }
                    }.map { post ->
                        TodayClosetItem(
                            todayClosetId = post.postId,
                            postId = post.postId,
                            frontImage = post.thumbnail,
                            backImage = post.thumbnail,
                            viewCount = 0,
                            profileImage = post.thumbnail
                        )
                    })

                    if (posts.isEmpty() || posts.last().createdAt.substring(0, 10) != todayDate) {
                        setupViewPager()
                    } else {
                        currentPage++
                        fetchRecentPosts(clositId, currentPage)
                    }
                } else {
                    Log.d("UPLOAD", "API Call Failed: ${response.message}")  // 실패한 경우 로그 추가
                }
            },
            onFailure = { t ->
                Log.e("UPLOAD", "API Call Failed: ${t.message}")  // 실패 로그 추가
                Toast.makeText(this, "게시글 불러오기 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )

    }


    // ViewPager2 설정
// ViewPager 설정
    private fun setupViewPager() {
        val fragments = todayPosts.map { post ->
            UploadFragment.newInstance(post.postId)  // UploadFragment에 postId 전달
        }

        val uploadAdapter = UploadAdapter(this, fragments)
        binding.photoViewPager.adapter = uploadAdapter

        // 화면 양 옆에 여백을 주기 위해 padding 설정
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val pageMarginPx = (screenWidth * 0.05).toInt() // 페이지 사이의 간격
        val offsetPx = pageMarginPx * 1 // 미리보기 효과를 위한 간격 설정

        binding.photoViewPager.apply {
            setPadding(pageMarginPx, 0, pageMarginPx, 0)  // 좌우 여백을 설정
            clipToPadding = false  // 패딩 부분이 잘리지 않도록 설정
            offscreenPageLimit = 3 // 한 번에 보여줄 페이지 수
        }

        // 페이지 전환 효과 설정
        binding.photoViewPager.setPageTransformer { page, position ->
            val pageTranslationX = -offsetPx * position
            page.translationX = pageTranslationX
        }
    }


    // 업로드 버튼 클릭 시 처리
// 업로드 버튼 클릭 시 API 호출 및 업로드 처리
    private fun uploadSelectedPost() {
        val currentItem = binding.photoViewPager.currentItem // 현재 보이는 아이템 위치
        if (currentItem < 0 || currentItem >= todayPosts.size) {
            Toast.makeText(this, "업로드할 게시글이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPostId = todayPosts[currentItem].postId

        val request = TodayClosetUploadRequest(selectedPostId)

        val apiCall = { RetrofitClient.todayClosetApiService.uploadTodayCloset(request) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    Toast.makeText(this, "게시물이 업로드되었습니다!", Toast.LENGTH_SHORT).show()
                    navigateToTimelineFragment() // 업로드 성공 후 타임라인 이동
                } else {
                    Toast.makeText(this, "업로드 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }

    // 업로드 성공 후 TimelineActivity로 이동
    private fun navigateToTimelineFragment() {
        val intent = Intent(this, TimelineActivity::class.java).apply {
            putExtra("navigateTo", "TodayClosetFragment") // 이동할 프래그먼트 정보 전달
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }

}
