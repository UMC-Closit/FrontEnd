package com.example.umc_closit.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.R
import com.example.umc_closit.data.entities.HighlightItem
import com.example.umc_closit.data.entities.RecentItem
import com.example.umc_closit.data.remote.FollowRequest
import com.example.umc_closit.data.remote.FollowResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.UnfollowResponse
import com.example.umc_closit.databinding.FragmentProfileBinding
import com.example.umc_closit.ui.login.LoginActivity
import com.example.umc_closit.ui.profile.highlight.HighlightAdapter
import com.example.umc_closit.ui.profile.history.HistoryActivity
import com.example.umc_closit.ui.profile.recent.RecentAdapter
import com.example.umc_closit.utils.DateUtils.getCurrentDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    // ViewBinding 선언
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var loggedInUserId: Int = -1  // 멤버 변수로 선언
    private var profileUserId: Int = -1  // 멤버 변수로 선언

    private var isFollowing = false


    // highlightAdapter를 클래스 멤버 변수로 선언
    private lateinit var highlightAdapter: HighlightAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // fragment_profile 레이아웃 바인딩
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkuser()

        // 화면 너비 가져오기
        val screenWidth = resources.displayMetrics.widthPixels

        // 샘플 데이터
        val highlightItems = mutableListOf(
            HighlightItem(R.drawable.img_profile_highlight, "24.12.07"),
            HighlightItem(R.drawable.img_profile_highlight, "24.12.08")
        )

        // Recent 이미지 리소스 리스트
        val recentItems = listOf(
            RecentItem(R.drawable.img_profile_recent, "Item 1"),
            RecentItem(R.drawable.img_profile_recent, "Item 2"),
            RecentItem(R.drawable.img_profile_recent, "Item 3"),
            RecentItem(R.drawable.img_profile_recent, "Item 4"),
            RecentItem(R.drawable.img_profile_recent, "Item 5")
        )

        val recentAdapter = RecentAdapter(recentItems, screenWidth)
        binding.rvRecent.adapter = recentAdapter

        // HighlightAdapter 초기화
        highlightAdapter = HighlightAdapter(
            highlightItems,
            {
                val newHighlight = HighlightItem(R.drawable.img_profile_highlight, getCurrentDate())
                highlightAdapter.updateItems(newHighlight)
            },
            screenWidth,
            isMyProfile() // 본인 프로필 여부
        )



        // RecyclerView 설정
        binding.rvHighlights.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHighlights.adapter = highlightAdapter
        binding.rvHighlights.setHasFixedSize(true)

        // RecyclerView 설정
        binding.rvRecent.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecent.adapter = recentAdapter
        binding.rvRecent.setHasFixedSize(true)


        // "히스토리" 클릭 시 히스토리 액티비티로 이동
        binding.tvHistory.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.tvLogout.setOnClickListener {
            logout()
        }

        binding.viewFollowBtn.setOnClickListener {
            toggleFollow()
        }

        binding.tvFollow.setOnClickListener {
            toggleFollow()
        }
    }

    private fun toggleFollow() {
        val followerId = loggedInUserId
        val followingId = profileUserId

        if (isFollowing) {
            unfollowUser(followerId, followingId)
        } else {
            followUser(followerId, followingId)
        }
    }

    private fun followUser(followerId: Int, followingId: Int) {
        val request = FollowRequest(follower = followerId, following = followingId)

        RetrofitClient.profileService.followUser(request).enqueue(object : Callback<FollowResponse> {
            override fun onResponse(call: Call<FollowResponse>, response: Response<FollowResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    isFollowing = true
                    updateFollowButtonUI(isFollowing)
                    Toast.makeText(requireContext(), "팔로우 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "팔로우 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FollowResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun unfollowUser(followerId: Int, followingId: Int) {
        RetrofitClient.profileService.unfollowUser(followerId, followingId).enqueue(object : Callback<UnfollowResponse> {
            override fun onResponse(call: Call<UnfollowResponse>, response: Response<UnfollowResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    isFollowing = false
                    updateFollowButtonUI(isFollowing)
                    Toast.makeText(requireContext(), "언팔로우 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "언팔로우 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UnfollowResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateFollowButtonUI(following: Boolean) {
        val backgroundDrawable = binding.viewFollowBtn.background.mutate() as android.graphics.drawable.GradientDrawable

        if (following) {
            binding.tvFollow.text = "팔로잉"
            backgroundDrawable.setColor(resources.getColor(R.color.black, null))
            binding.tvFollow.setTextColor(resources.getColor(R.color.white, null))
        } else {
            binding.tvFollow.text = "팔로우"
            backgroundDrawable.setColor(resources.getColor(R.color.white, null))
            binding.tvFollow.setTextColor(resources.getColor(R.color.black, null))
        }
    }


    private fun isMyProfile(): Boolean {
        return loggedInUserId != -1 && (profileUserId == -1 || loggedInUserId == profileUserId)
    }


    private fun checkuser() {
        val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getInt("userId", -1)
        profileUserId = arguments?.getInt("profileUserId", -1) ?: -1

        Log.d("userinfo", "loggedInUserId: $loggedInUserId, profileUserId: $profileUserId, isMyProfile: ${isMyProfile()}")

        if (isMyProfile()) {
            binding.viewFollowBtn.visibility = View.GONE
            binding.tvFollow.visibility = View.GONE
            binding.clSettingsContainer.visibility = View.VISIBLE
        } else {
            binding.viewFollowBtn.visibility = View.VISIBLE
            binding.tvFollow.visibility = View.VISIBLE
            binding.clSettingsContainer.visibility = View.GONE
        }
    }


    private fun logout() {
        val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // 저장된 로그인 정보 삭제

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent) // 로그인 화면으로 이동
        requireActivity().finishAffinity() // 현재 액티비티 종료 (모든 백 스택 제거)
    }


}
