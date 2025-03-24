package com.example.umc_closit.ui.profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.FollowRequest
import com.example.umc_closit.data.remote.profile.FollowResponse
import com.example.umc_closit.data.remote.profile.UnfollowResponse
import com.example.umc_closit.databinding.DialogQuitBinding
import com.example.umc_closit.databinding.FragmentProfileBinding
import com.example.umc_closit.ui.profile.follow.FollowListActivity
import com.example.umc_closit.ui.login.LoginActivity
import com.example.umc_closit.ui.profile.edit.EditProfileActivity
import com.example.umc_closit.ui.profile.highlight.HighlightAdapter
import com.example.umc_closit.ui.profile.highlight.HighlightDetailActivity
import com.example.umc_closit.ui.profile.history.HistoryActivity
import com.example.umc_closit.ui.profile.posts.SavedPostsActivity
import com.example.umc_closit.ui.profile.recent.RecentAdapter
import com.example.umc_closit.ui.profile.recent.RecentDetailActivity
import com.example.umc_closit.utils.TokenUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var loggedInUserClositId: String = ""
    private var profileUserClositId: String = ""
    private var isFollowing = false

    private lateinit var highlightAdapter: HighlightAdapter
    private lateinit var recentAdapter: RecentAdapter


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
        loadUserHighlights()
        loadRecentPosts()

        binding.tvNoHighlight.visibility = View.GONE
        binding.tvNoRecent.visibility = View.GONE

        if (isMyProfile()) {
            binding.tvEditProfileImage.visibility = View.VISIBLE
        } else {
            binding.tvEditProfileImage.visibility = View.GONE
        }

        binding.tvEditProfileImage.setOnClickListener {
            openGallery()
        }

        binding.tvFollowingLabel.setOnClickListener {
            val intent = Intent(requireContext(), FollowListActivity::class.java)
            if (isMyProfile()) {
                // 내 프로필일 때 팔로잉 목록으로 이동
                intent.putExtra("listType", "following")  // 팔로잉 목록
            } else {
                // 타인의 프로필일 때 타인의 팔로잉 목록으로 이동
                intent.putExtra("profileUserClositId", profileUserClositId) // 타인의 clositId 전달
                intent.putExtra("listType", "following")  // 팔로잉 목록
            }
            startActivity(intent)
        }

        binding.tvFollowersLabel.setOnClickListener {
            val intent = Intent(requireContext(), FollowListActivity::class.java)
            if (isMyProfile()) {
                // 내 프로필일 때 팔로워 목록으로 이동
                intent.putExtra("listType", "followers")  // 팔로워 목록
            } else {
                // 타인의 프로필일 때 타인의 팔로워 목록으로 이동
                intent.putExtra("profileUserClositId", profileUserClositId) // 타인의 clositId 전달
                intent.putExtra("listType", "followers")  // 팔로워 목록
            }
            startActivity(intent)
        }

        // 유저 정보 불러오기
        loadUserProfile()
        checkFollowStatus()

        val screenWidth = resources.displayMetrics.widthPixels

        highlightAdapter = HighlightAdapter(
            items = mutableListOf(),
            onAddClick = {
                startActivity(Intent(requireContext(), HistoryActivity::class.java))
            },
            onItemClick = { item ->
                val postIdList = highlightAdapter.getPostIdList()
                val clickedPosition = postIdList.indexOf(item.postId)

                val intent = Intent(requireContext(), HighlightDetailActivity::class.java)
                intent.putIntegerArrayListExtra("postIdList", ArrayList(postIdList))
                intent.putExtra("clickedPosition", clickedPosition)
                startActivity(intent)
            },
            screenWidth = screenWidth,
            isMyProfile = isMyProfile()
        )

        recentAdapter = RecentAdapter(
            items = emptyList(),
            screenWidth = resources.displayMetrics.widthPixels
        ) { postId ->
            val postIdList = recentAdapter.getPostIdList()
            val clickedPosition = postIdList.indexOf(postId)

            val intent = Intent(requireContext(), RecentDetailActivity::class.java).apply {
                putIntegerArrayListExtra("postIdList", ArrayList(postIdList))
                putExtra("clickedPosition", clickedPosition)
            }
            startActivity(intent)
        }


        binding.rvRecent.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recentAdapter
            setHasFixedSize(true)
        }


        binding.rvHighlights.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = highlightAdapter
            setHasFixedSize(true)
        }

        binding.tvHistory.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }

        binding.tvEditInfo.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
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

    private fun updateUI() {
        // 하이라이트가 비어있으면 tv_no_highlight 보이기
        if (highlightAdapter.itemCount == 0) {
            binding.tvNoHighlight.visibility = View.VISIBLE
        } else {
            binding.tvNoHighlight.visibility = View.GONE
        }

        // 최근 게시물이 없으면 tv_no_recent 보이기
        if (recentAdapter.itemCount == 0) {
            binding.tvNoRecent.visibility = View.VISIBLE
        } else {
            binding.tvNoRecent.visibility = View.GONE
        }
    }


    private fun loadRecentPosts() {
        val apiCall = { RetrofitClient.postService.getRecentPosts(profileUserClositId, 0) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    val recentPosts = response.result.userRecentPostDTOList
                    Log.d("RECENT","${recentPosts.size}")
                    recentAdapter.updateItems(recentPosts)

                    // 최근 게시글이 없으면 텍스트뷰 표시
                    if (recentPosts.isEmpty()) {
                        binding.tvNoRecent.visibility = View.VISIBLE
                    } else {
                        binding.tvNoRecent.visibility = View.GONE
                    }
                    // UI 업데이트 (하이라이트 및 최근 게시물 확인)
                    updateUI()
                }
            },
            onFailure = { t ->
                Log.e("ProfileFragment", "최근 게시물 불러오기 실패: ${t.message}")
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }




    private fun loadUserHighlights() {
        val apiCall = { RetrofitClient.profileService.getHighlights(profileUserClositId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    val highlights = response.result.highlights
                    highlightAdapter.setItems(highlights)

                    // 하이라이트가 비어있으면 텍스트 보이기
                    if (highlights.isEmpty()) {
                        binding.tvNoHighlight.visibility = View.VISIBLE
                    } else {
                        binding.tvNoHighlight.visibility = View.GONE
                    }
                    updateUI()
                }
            },
            onFailure = { t ->
                Log.e("ProfileFragment", "하이라이트 불러오기 실패: ${t.message}")
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageUri = data?.data
            imageUri?.let {
                uploadProfileImage(it)
            }
        }
    }

    private fun uploadProfileImage(imageUri: Uri) {
        val clositId = loggedInUserClositId

        try {
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            inputStream?.use { input ->
                val requestFile = input.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
                val fileName = "${clositId}_profile_${System.currentTimeMillis()}.jpg"
                val body = MultipartBody.Part.createFormData("user_image", fileName, requestFile)

                val apiCall = { RetrofitClient.profileService.uploadProfileImage(clositId, body) }

                TokenUtils.handleTokenRefresh(
                    call = apiCall(),
                    onSuccess = { response ->
                        if (response.isSuccess) {
                            Toast.makeText(requireContext(), "프로필 이미지 변경 성공", Toast.LENGTH_SHORT).show()
                            loadUserProfile()
                        } else {
                            Toast.makeText(requireContext(), "프로필 이미지 변경 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PROFILE_IMAGE", "프로필 이미지 변경 실패: ${response.message}")
                        }
                    },
                    onFailure = { t ->
                        Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("PROFILE_IMAGE", "네트워크 오류", t)
                    },
                    retryCall = apiCall,
                    context = requireContext()
                )
            } ?: Log.e("PROFILE_IMAGE", "inputStream이 null임")
        } catch (e: Exception) {
            Log.e("PROFILE_IMAGE", "Exception 발생", e)
        }
    }



    companion object {
        private const val GALLERY_REQUEST_CODE = 100
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

                    // 팔로워 수 증가
                    val currentFollowers = binding.tvFollowersCount.text.toString().toInt()
                    binding.tvFollowersCount.text = (currentFollowers + 1).toString()

                } else {
                    Toast.makeText(requireContext(), "팔로우 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("FOLLOW","$t")
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
                    // 팔로워 수 감소
                    val currentFollowers = binding.tvFollowersCount.text.toString().toInt()
                    binding.tvFollowersCount.text = (currentFollowers - 1).toString()
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
                        .load(user.profileImage + "?ts=${System.currentTimeMillis()}")
                        .placeholder(R.drawable.img_profile_user)
                        .error(R.drawable.img_profile_user)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.ivProfileImage)


                    // 팔로워, 팔로잉 수 업데이트
                    binding.tvFollowersCount.text = user.followers.toString()
                    binding.tvFollowingCount.text = user.following.toString()

                    // 기록일 수 업데이트
                    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    val createdAtDate = dateFormat.parse(user.createdAt.substring(0, 10))
                    val today = Date()
                    val daysBetween = ((today.time - createdAtDate.time) / (1000 * 60 * 60 * 24)).toInt() + 1

                    binding.tvRecordDays.text = getString(R.string.record_days_format, daysBetween)

                } else {
                    Toast.makeText(requireContext(), "프로필 정보 로드 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("PROFILE","네트워크 오류: ${t.message}")
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }



    private fun updateFollowButtonUI(following: Boolean) {
        val backgroundDrawable = binding.viewFollowBtn.background.mutate() as android.graphics.drawable.GradientDrawable

        if (following) {
            binding.tvFollow.text = "팔로잉"
            backgroundDrawable.setColor(resources.getColor(R.color.pink_point, null))
            backgroundDrawable.setStroke(2, resources.getColor(R.color.pink_point, null)) // 테두리 변경
            binding.tvFollow.setTextColor(resources.getColor(R.color.white, null))
        } else {
            binding.tvFollow.text = "팔로우"
            backgroundDrawable.setColor(resources.getColor(R.color.black, null))
            backgroundDrawable.setStroke(2, resources.getColor(R.color.white, null)) // 테두리 변경
            binding.tvFollow.setTextColor(resources.getColor(R.color.white, null))
        }
    }


    private fun checkUser() {
        val sp = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        loggedInUserClositId = sp.getString("clositId", "") ?: ""
        profileUserClositId = arguments?.getString("profileUserClositId", "") ?: ""

        if (loggedInUserClositId == profileUserClositId) {
            // 내 프로필이면 수정 관련 버튼 보이게
            binding.clSettingsContainer.visibility = View.VISIBLE
            binding.viewFollowBtn.visibility = View.GONE
            binding.tvFollow.visibility = View.GONE
        } else {
            // 다른 사람 프로필이면 팔로우 버튼 보이게
            binding.clSettingsContainer.visibility = View.GONE
            binding.viewFollowBtn.visibility = View.VISIBLE
            binding.tvFollow.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        loadUserHighlights() // 하이라이트 목록 다시 불러오기
    }


    private fun logout() {
        TokenUtils.clearTokens(requireContext())
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finishAffinity()
    }
}
