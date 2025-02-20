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
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.entities.HighlightItem
import com.example.umc_closit.data.entities.RecentItem
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.FollowRequest
import com.example.umc_closit.data.remote.profile.FollowResponse
import com.example.umc_closit.data.remote.profile.ProfileUserResponse
import com.example.umc_closit.data.remote.profile.UnfollowResponse
import com.example.umc_closit.databinding.DialogQuitBinding
import com.example.umc_closit.databinding.FragmentProfileBinding
import com.example.umc_closit.ui.login.LoginActivity
import com.example.umc_closit.ui.profile.highlight.HighlightAdapter
import com.example.umc_closit.ui.profile.history.HistoryActivity
import com.example.umc_closit.ui.profile.posts.SavedPostsActivity
import com.example.umc_closit.ui.profile.recent.RecentAdapter
import com.example.umc_closit.utils.DateUtils.getCurrentDate
import com.example.umc_closit.utils.TokenUtils
import com.example.umc_closit.ui.mission.MissionActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var loggedInUserClositId: String = ""
    private var profileUserClositId: String = ""
    private var isFollowing = false

    private lateinit var highlightAdapter: HighlightAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUser()

        // 유저 정보 불러오기
        loadUserProfile()
        checkFollowStatus()

        val screenWidth = resources.displayMetrics.widthPixels

        val highlightItems = mutableListOf(
            HighlightItem(R.drawable.img_profile_highlight, "24.12.07"),
            HighlightItem(R.drawable.img_profile_highlight, "24.12.08")
        )

        val recentItems = listOf(
            RecentItem(R.drawable.img_profile_recent, "Item 1"),
            RecentItem(R.drawable.img_profile_recent, "Item 2")
        )

        val recentAdapter = RecentAdapter(recentItems, screenWidth)
        binding.rvRecent.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recentAdapter
            setHasFixedSize(true)
        }

        highlightAdapter = HighlightAdapter(
            highlightItems,
            {
                val newHighlight = HighlightItem(R.drawable.img_profile_highlight, getCurrentDate())
                highlightAdapter.updateItems(newHighlight)
            },
            screenWidth,
            isMyProfile()
        )

        binding.rvHighlights.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = highlightAdapter
            setHasFixedSize(true)
        }

        binding.tvHistory.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }

        binding.tvEditInfo.setOnClickListener {
            startActivity(Intent(requireContext(), MissionActivity::class.java))
        }

        binding.tvSavePosts.setOnClickListener {
            startActivity(Intent(requireContext(), SavedPostsActivity::class.java))
        }

        binding.tvLogout.setOnClickListener {
            logout()
        }

        binding.tvQuit.setOnClickListener {
            val clositId = TokenUtils.getClositId(requireContext()) ?: ""
            showQuitDialog(clositId) {}
        }

        binding.viewFollowBtn.setOnClickListener {
            toggleFollow()
        }
        binding.tvFollow.setOnClickListener {
            toggleFollow()
        }
    }

    private fun toggleFollow() {
        if (isFollowing) {
            unfollowUser()
        } else {
            followUser()
        }
    }

    private fun isMyProfile(): Boolean {
        return loggedInUserClositId.isNotEmpty() && loggedInUserClositId == profileUserClositId
    }


    private fun followUser() {
        val apiCall = {
            RetrofitClient.profileService.followUser(FollowRequest(profileUserClositId))
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

    private fun unfollowUser() {
        val apiCall = {
            RetrofitClient.profileService.unfollowUser(profileUserClositId)
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

    private fun checkFollowStatus() {
        val apiCall = {
            RetrofitClient.profileService.checkFollowStatus(profileUserClositId)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    isFollowing = response.result
                    updateFollowButtonUI(isFollowing)
                }
            },
            onFailure = { t ->
                Log.e("FollowStatus", "팔로우 여부 확인 실패: ${t.message}")
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }

    private fun loadUserProfile() {
        if (profileUserClositId.isEmpty()) return

        val apiCall = {
            RetrofitClient.profileService.getUserProfile(profileUserClositId)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    val user = response.result

                    // UI 업데이트
                    binding.tvUsername.text = user.name ?: "UNKNOWN"

                    Glide.with(requireContext())
                        .load(user.profileImage ?: R.drawable.img_profile_user)
                        .placeholder(R.drawable.img_profile_user)
                        .error(R.drawable.img_profile_user)
                        .into(binding.ivProfileImage)
                } else {
                    Toast.makeText(requireContext(), "프로필 정보 로드 실패: ${response.message}", Toast.LENGTH_SHORT).show()
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

    private fun checkUser() {
        val sp = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        loggedInUserClositId = sp.getString("clositId", "") ?: ""
        profileUserClositId = arguments?.getString("profileUserClositId", "") ?: ""

        if (loggedInUserClositId == profileUserClositId) {
            binding.viewFollowBtn.visibility = View.GONE
        }
    }

    private fun showQuitDialog(clositId: String, onSuccess: () -> Unit) {
        val dialog = Dialog(requireContext())
        val binding = DialogQuitBinding.inflate(layoutInflater)

        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
        val apiCall = {
            RetrofitClient.authService.deleteUser()
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                onResult(response.isSuccess)
            },
            onFailure = {
                onResult(false)
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }



    private fun logout() {
        TokenUtils.clearTokens(requireContext())
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finishAffinity()
    }
}
