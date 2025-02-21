package com.example.umc_closit.ui.upload

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.battle.TodayClosetItem
import com.example.umc_closit.databinding.ActivityUploadBinding
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
                if (response.isSuccess) {
                    val posts = response.result.userRecentPostDTOList
                    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    // 오늘 날짜에 해당하는 게시물만 필터링
                    todayPosts.addAll(posts.filter { post ->
                        try {
                            // 여러 날짜 형식 처리: yyyy-MM-dd, yyyy/MM/dd
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val postDate = post.createdAt.replace("/", "-") // / 를 -로 변경하여 처리
                            val parsedDate = dateFormat.parse(postDate)

                            // 오늘 날짜와 비교
                            val postDateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDate)
                            postDateFormatted == todayDate // 오늘 날짜에 해당하는 게시물만
                        } catch (e: Exception) {
                            Log.e("UPLOAD", "Date parsing error: ${e.message}")
                            false
                        }
                    }.map { post ->
                        // UserRecentPostDTO에서 TodayClosetItem으로 변환
                        TodayClosetItem(
                            todayClosetId = post.postId,  // 해당 데이터를 TodayClosetItem에 맞게 변환
                            postId = post.postId,
                            frontImage = post.thumbnail, // 예시로 frontImage에 thumbnail을 사용
                            backImage = post.thumbnail,  // backImage도 같은 이미지로 할 경우
                            viewCount = 0,  // 임의로 0으로 설정
                            profileImage = post.thumbnail // 임의로 profileImage에 thumbnail을 설정
                        )
                    })

                    // 더 이상 오늘 날짜의 게시글이 나오지 않으면 멈추기
                    if (posts.isEmpty() || posts.last().createdAt.substring(0, 10) != todayDate) {
                        setupViewPager()  // 오늘 날짜의 게시글만 있으면 뷰페이저 설정
                    } else {
                        currentPage++ // 다음 페이지로 넘어가기
                        fetchRecentPosts(clositId, currentPage)  // 페이지네이션을 통해 계속해서 데이터 가져오기
                    }
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "게시글 불러오기 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }


    // ViewPager2 설정
    private fun setupViewPager() {
        Log.d("UPLOAD", "Setting up ViewPager with posts: ${todayPosts.size}")  // ViewPager 설정 전에 todayPosts 크기 확인
        val fragments = todayPosts.map { post ->
            Log.d("UPLOAD", "Passing postId: ${post.postId}")  // 여기도 각 postId 출력
            UploadFragment.newInstance(post.postId)  // UploadFragment에 postId 전달
        }
        val uploadAdapter = UploadAdapter(this, fragments)
        binding.photoViewPager.adapter = uploadAdapter
    }



    // 업로드 버튼 클릭 시 처리
    private fun uploadSelectedPost() {
        // 업로드 처리
        Toast.makeText(this, "게시물이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
    }
}
