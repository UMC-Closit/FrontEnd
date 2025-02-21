package com.example.umc_closit.ui.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.databinding.FragmentUploadBinding
import com.example.umc_closit.utils.TokenUtils

class UploadFragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding

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
        loadPostDetail(postId)
    }

    // 게시글 상세 조회 후 이미지 로드
    private fun loadPostDetail(postId: Int) {
        val apiCall = { RetrofitClient.postService.getPostDetail(postId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    val post = response.result
                    // frontImage와 backImage를 Glide로 로드
                    Glide.with(requireContext())
                        .load(post.frontImage)
                        .into(binding.ivImageBig)

                    Glide.with(requireContext())
                        .load(post.backImage)
                        .into(binding.ivImageSmall)
                }
            },
            onFailure = { t ->
                // 오류 처리
            },
            retryCall = apiCall,
            context = requireContext()
        )
    }
}
