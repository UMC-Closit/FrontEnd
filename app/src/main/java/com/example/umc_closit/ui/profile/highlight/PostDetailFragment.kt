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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.post.PostDetail
import com.example.umc_closit.databinding.FragmentPostDetailBinding
import com.example.umc_closit.utils.TokenUtils
import kotlinx.coroutines.launch

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private var postId: Int = -1
    private var isSaved: Boolean = false

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

        fetchPostDetail(postId)

        binding.buttonWithIcon.setOnClickListener {
            if (isSaved) {
                deleteHighlight(postId)
            } else {
                createHighlight(postId)
            }
        }
    }


    private fun fetchPostDetail(postId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.postService.getPostDetail(postId)
                if (response.isSuccessful && response.body() != null) {
                    val post = response.body()!!.result
                    isSaved = post.isSaved // 서버에서 가져온 초기 상태 반영
                    updateUI(post)
                    updateHighlightButtonUI()
                }
            } catch (e: Exception) {
                Log.e("PostDetailFragment", "게시글 상세 조회 실패: ${e.message}")
            }
        }
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

            displayHashtags(post.hashtags)

            // visibility 드롭다운 설정
            setupDropdown(post.visibility)
        }
    }

    private fun displayHashtags(hashtags: List<String>) {
        val flow = binding.flowHashtagContainer
        val parentLayout = binding.clHashtag
        val ids = mutableListOf<Int>()

        parentLayout.removeAllViews() // 중복 생성 방지

        for (hashtag in hashtags) {
            val textView = TextView(requireContext()).apply {
                id = View.generateViewId()
                text = "#$hashtag"
                textSize = 16f
                typeface = ResourcesCompat.getFont(requireContext(), R.font.noto_regular)
                includeFontPadding = false
                setTextColor(ContextCompat.getColor(context, R.color.white))
                setBackgroundResource(R.drawable.bg_detail_hashtag)
                setPadding(30, 8, 30, 8)
            }
            parentLayout.addView(textView)
            ids.add(textView.id)
        }

        flow.referencedIds = ids.toIntArray()
        flow.invalidate()
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
            if (isSaved) {
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
                    isSaved = true
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
                    isSaved = false
                    updateHighlightButtonUI()
                    Toast.makeText(requireContext(), "하이라이트 삭제 성공", Toast.LENGTH_SHORT).show()
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
    fun newInstance(postId: Int): PostDetailFragment {
        return PostDetailFragment().apply {
            arguments = Bundle().apply {
                putInt("postId", postId)
            }
        }
    }
}

}


