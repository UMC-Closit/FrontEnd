package com.example.umc_closit.ui.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.post.ItemTag
import com.example.umc_closit.data.remote.post.PostDetail
import com.example.umc_closit.databinding.FragmentUploadBinding
import com.example.umc_closit.utils.FileUtils
import com.example.umc_closit.utils.TokenUtils

class UploadFragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding
    private var isFrontImageBig = true  // 현재 큰 이미지가 앞면인지 여부
    private var isTagVisible = false    // 태그 표시 여부
    private lateinit var post: PostDetail  // 게시글 데이터

    companion object {
        private const val ARG_POST_ID = "postId"

        fun newInstance(postId: Int): UploadFragment {
            val fragment = UploadFragment()
            val args = Bundle()
            args.putInt(ARG_POST_ID, postId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getInt(ARG_POST_ID) ?: return
        loadPostDetail(postId) // 게시글 데이터 로드
    }

    // 게시글 상세 조회 후 이미지 로드
    private fun loadPostDetail(postId: Int) {
        val apiCall = { RetrofitClient.postService.getPostDetail(postId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    post = response.result  // post 데이터 저장
                    Glide.with(requireContext()).load(post.frontImage).into(binding.ivImageBig)
                    Glide.with(requireContext()).load(post.backImage).into(binding.ivImageSmall)

                    setupImageClickListeners()
                }
            },
            onFailure = { t ->
                // 오류 처리
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }

    // 이미지 클릭 리스너 설정
    private fun setupImageClickListeners() {
        // 큰 이미지 클릭 시 태그 표시/숨김
        binding.ivImageBig.setOnClickListener {
            if (isTagVisible) {
                hideTags()
            } else {
                val tags = if (isFrontImageBig) post.frontItemtags else post.backItemtags
                showTags(tags)
            }
        }

        // 작은 이미지 클릭 시 이미지 스왑
        binding.ivImageSmall.setOnClickListener {
            swapImagesWithTags()
        }
    }

    // 태그 보이기
    private fun showTags(tags: List<ItemTag>) {
        if (tags.isNotEmpty()) {
            FileUtils.addItemTags(
                context = requireContext(),
                container = binding.clTagContainer,
                imageView = binding.ivImageBig,
                tags = tags
            )
            isTagVisible = true
            binding.clTagContainer.alpha = 1f // 태그 컨테이너 표시
        }
    }

    // 태그 숨기기
    private fun hideTags() {
        binding.clTagContainer.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.clTagContainer.removeAllViews()
                isTagVisible = false
            }
            .start()
    }

    // 이미지 스왑 후 태그 상태 초기화
    private fun swapImagesWithTags() {
        FileUtils.swapImagesWithTagEffect(
            bigImageView = binding.ivImageBig,
            smallImageView = binding.ivImageSmall,
            tagContainer = binding.clTagContainer
        ) {
            isFrontImageBig = !isFrontImageBig  // 현재 큰 이미지 변경
            isTagVisible = false  // 태그 숨기기
            binding.clTagContainer.alpha = 0f  // 태그 컨테이너 숨김 처리
            binding.clTagContainer.removeAllViews() // 태그 제거
        }
    }
}
