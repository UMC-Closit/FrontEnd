package com.example.umc_closit.ui.profile.highlight

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.post.ItemTag
import com.example.umc_closit.data.remote.post.PostDetail
import com.example.umc_closit.databinding.FragmentPostDetailBinding
import com.example.umc_closit.utils.FileUtils.addItemTagView
import com.example.umc_closit.utils.FileUtils.swapImagesWithTagEffect
import com.example.umc_closit.utils.HashtagUtils
import com.example.umc_closit.utils.TokenUtils
import kotlinx.coroutines.launch

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private var postId: Int = -1
    private var isHighlighted: Boolean = false
    private var isFrontImageBig = true // 전면이 큰 사진인지
    private var isTagVisible = false   // 현재 태그 보이는지


    private var frontItemTags: List<ItemTag> = emptyList()
    private var backItemTags: List<ItemTag> = emptyList()
    private var hashtags: List<String> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getInt("postId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val showButton = arguments?.getBoolean("showButton") ?: true
        binding.buttonWithIcon.visibility = if (showButton) View.VISIBLE else View.GONE

        fetchPostDetail(postId)

        binding.buttonWithIcon.setOnClickListener {
            if (isHighlighted) {
                deleteHighlight(postId)
            } else {
                createHighlight(postId)
            }
        }

        binding.ivImageBig.setOnClickListener {
            if (isTagVisible) {
                // 태그 숨김
                binding.clTagContainer.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction {
                        binding.clTagContainer.removeAllViews()
                        isTagVisible = false
                    }
                    .start()
            } else {
                // 태그 표시
                val tags = if (isFrontImageBig) frontItemTags else backItemTags
                binding.clTagContainer.removeAllViews()

                for (tag in tags) {
                    addItemTagView(requireContext(), binding.clTagContainer, binding.ivImageBig, tag)
                }

                binding.clTagContainer.alpha = 0f
                binding.clTagContainer.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()

                isTagVisible = true
            }
        }


        binding.ivImageSmall.setOnClickListener {
            swapImagesWithTagEffect(
                bigImageView = binding.ivImageBig,
                smallImageView = binding.ivImageSmall,
                tagContainer = binding.clTagContainer
            ) {
                isFrontImageBig = !isFrontImageBig
                isTagVisible = false // 스왑하면 태그 숨김 상태로 리셋
                binding.clTagContainer.alpha = 0f // 투명도 0으로 만들어 둬
            }
        }

    }

    private fun fetchPostDetail(postId: Int) {
        val apiCall = { RetrofitClient.postService.getPostDetail(postId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    val post = response.result
                    isHighlighted = post.isHighlighted

                    frontItemTags = post.frontItemtags
                    backItemTags = post.backItemtags

                    hashtags = post.hashtags
                    HashtagUtils.displayHashtags(
                        context = requireContext(),
                        hashtags = hashtags,
                        flow = binding.flowHashtagContainer,
                        parentLayout = binding.clHashtag
                    )

                    updateUI(post)
                    updateHighlightButtonUI()
                } else {
                    Toast.makeText(requireContext(), "게시글 불러오기 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }


    private fun updateUI(post: PostDetail) {
        with(binding) {
            Glide.with(requireContext()).load(post.frontImage).into(ivImageBig)
            Glide.with(requireContext()).load(post.backImage).into(ivImageSmall)

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(post.pointColor))
                setStroke(2, Color.BLACK)
            }
            viewColorCircle.background = drawable

            HashtagUtils.displayHashtags(
                context = requireContext(),
                hashtags = post.hashtags,
                flow = binding.flowHashtagContainer,
                parentLayout = binding.clHashtag
            )


            // visibility 드롭다운 설정
            setupDropdown(post.visibility)
        }
    }


    private fun setupDropdown(visibility: String) {
        val options = listOf("전체 공개", "친구 공개", "비공개")
        val mappedVisibility = when (visibility) {
            "PUBLIC" -> "전체 공개"
            "FRIEND" -> "친구 공개"
            "PRIVATE" -> "비공개"
            else -> ""
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, options)
        binding.exposedDropdown.setAdapter(adapter)
        binding.exposedDropdown.setText(mappedVisibility, false)
        binding.exposedDropdown.isEnabled = false // 변경 불가하게 설정
    }

    private fun updateHighlightButtonUI() {
        with(binding.buttonWithIcon) {
            if (isHighlighted) {
                text = "하이라이트에 추가됨"
                setBackgroundColor(Color.BLACK)
                setTextColor(Color.WHITE)
                iconTint = ContextCompat.getColorStateList(context, R.color.white)

            } else {
                text = "하이라이트에 추가"
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pink_point))
                setTextColor(Color.WHITE)
                iconTint = ContextCompat.getColorStateList(context, R.color.white)
            }
        }
    }

    private fun createHighlight(postId: Int) {
        val apiCall = {
            RetrofitClient.profileService.createHighlight(mapOf("post" to postId))
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    isHighlighted = true
                    updateHighlightButtonUI()
                    Toast.makeText(requireContext(), "하이라이트 추가 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "하이라이트 추가 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("HIGHLIGHT","$t")
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }

    private fun deleteHighlight(postId: Int) {
        val apiCall = {
            RetrofitClient.profileService.deleteHighlight(postId)
        }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    isHighlighted = false
                    updateHighlightButtonUI()
                } else {
                    Toast.makeText(requireContext(), "하이라이트 삭제 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }




    companion object {
        fun newInstance(postId: Int, showButton: Boolean): PostDetailFragment {
            return PostDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("postId", postId)
                    putBoolean("showButton", showButton)
                }
            }
        }
    }

}


