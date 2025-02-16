package com.example.umc_closit.ui.profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.FollowRequest
import com.example.umc_closit.data.remote.profile.FollowResponse
import com.example.umc_closit.data.remote.profile.UnfollowResponse
import com.example.umc_closit.databinding.DialogQuitBinding
import com.example.umc_closit.databinding.FragmentProfileBinding
import com.example.umc_closit.ui.login.LoginActivity
import com.example.umc_closit.ui.profile.edit.EditProfileActivity
import com.example.umc_closit.ui.profile.highlight.HighlightAdapter
import com.example.umc_closit.ui.profile.history.HistoryActivity
import com.example.umc_closit.ui.profile.posts.SavedPostsActivity
import com.example.umc_closit.ui.profile.recent.RecentAdapter
import com.example.umc_closit.utils.DateUtils.getCurrentDate
import com.example.umc_closit.utils.TokenUtils

class ProfileFragment : Fragment() {

    // ViewBinding 선언
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var loggedInUserClositId: String = ""  // 멤버 변수로 선언
    private var profileUserClositId: String = ""  // 멤버 변수로 선언

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

        // 내 정보 수정
        binding.tvEditInfo.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        // 저장된 게시글
        binding.tvSavePosts.setOnClickListener {
            val intent = Intent(requireContext(), SavedPostsActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃
        binding.tvLogout.setOnClickListener {
            logout()
        }

        // 탈퇴하기
        binding.tvQuit.setOnClickListener {
            val clositId = TokenUtils.getClositId(requireContext()) ?: ""
            showQuitDialog(clositId) {
                // 탈퇴 성공 후 처리
            }
        }



        binding.viewFollowBtn.setOnClickListener {
            toggleFollow()
        }

        binding.tvFollow.setOnClickListener {
            toggleFollow()
        }
    }

    private fun toggleFollow() {
        val followerClositId = loggedInUserClositId
        val followingClositId = profileUserClositId

        if (isFollowing) {
            unfollowUser(followerClositId, followingClositId)
        } else {
            followUser(followerClositId, followingClositId)
        }
    }

    private fun followUser(followerClositId: String, followingClositId: String) {
        val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val token = "Bearer ${sharedPreferences.getString("accessToken", "") ?: ""}"

        val request = FollowRequest(follower = followerClositId, following = followingClositId)

        val apiCall = {
            RetrofitClient.profileService.followUser(token, request)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response: FollowResponse ->
                if (response.isSuccess) {
                    isFollowing = true
                    updateFollowButtonUI(isFollowing)
                    Toast.makeText(requireContext(), "팔로우 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "팔로우 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }

    private fun unfollowUser(followerClositId: String, followingClositId: String) {
        val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val token = "Bearer ${sharedPreferences.getString("accessToken", "") ?: ""}"

        val apiCall = {
            RetrofitClient.profileService.unfollowUser(token, followerClositId, followingClositId)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response: UnfollowResponse ->
                if (response.isSuccess) {
                    isFollowing = false
                    updateFollowButtonUI(isFollowing)
                    Toast.makeText(requireContext(), "언팔로우 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "언팔로우 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = requireContext()
        )
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
        return loggedInUserClositId.isNotEmpty() && (profileUserClositId.isEmpty() || loggedInUserClositId == profileUserClositId)
    }


    private fun checkuser() {
        val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        loggedInUserClositId = sharedPreferences.getString("clositId", "") ?: ""
        profileUserClositId = arguments?.getString("profileUserClositId", "") ?: ""

        Log.d(
            "userinfo",
            "loggedInUserClositId: $loggedInUserClositId, profileUserClositId: $profileUserClositId, isMyProfile: ${isMyProfile()}"
        )

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

    private fun showQuitDialog(clositId: String, onSuccess: () -> Unit) {
        val dialog = Dialog(requireContext())
        val binding = DialogQuitBinding.inflate(layoutInflater)

        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명 처리

        binding.etId.hint = clositId

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            val inputId = binding.etId.text.toString().trim()
            if (inputId == clositId) {
                binding.tvQuitError.visibility = View.GONE
                deleteUser { success ->
                    if (success) {
                        TokenUtils.clearTokens(requireContext())
                        onSuccess()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        requireActivity().finishAffinity()
                    } else {
                        Toast.makeText(requireContext(), "탈퇴 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            } else {
                binding.tvQuitError.visibility = View.VISIBLE
            }
        }

        dialog.show()
    }


    private fun deleteUser(onResult: (Boolean) -> Unit) {
        val token = TokenUtils.getAccessToken(requireContext())
        if (token.isNullOrEmpty()) {
            onResult(false)
            return
        }

        val apiCall = {
            RetrofitClient.authService.deleteUser("Bearer $token")
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            },
            onFailure = {
                onResult(false)
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }


}
