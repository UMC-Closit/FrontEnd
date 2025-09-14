package com.example.umc_closit.ui.upload

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.post.ItemTag
import com.example.umc_closit.data.remote.post.PostDetail
import com.example.umc_closit.databinding.FragmentUploadBinding
import com.example.umc_closit.databinding.CustomTagDialogBinding
import com.example.umc_closit.utils.FileUtils
import com.example.umc_closit.utils.TokenUtils

class UploadFragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding
    private var isFrontImageBig = true  // 현재 큰 이미지가 앞면인지 여부
    private var isTagVisible = false    // 태그 표시 여부
    private var isColorExtractMode = false  // 색상 추출 모드 여부
    private lateinit var post: PostDetail  // 게시글 데이터
    private var originalBitmap: Bitmap? = null  // 원본 비트맵
    private val hashtags = mutableListOf<String>()  // 해시태그 리스트

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
        
        // viewColorIcon 클릭 리스너 설정
        binding.viewColorIcon.setOnClickListener {
            isColorExtractMode = !isColorExtractMode
        }
        
        // btnHashtag 클릭 리스너 설정
        binding.btnHashtag.setOnClickListener {
            showHashtagDialog { newHashtag ->
                hashtags.add(newHashtag)
                createHashtagTextView(newHashtag, binding.clHashtag, binding.flowHashtagContainer)
            }
        }
    }

    // 게시글 상세 조회 후 이미지 로드
    private fun loadPostDetail(postId: Int) {
        val apiCall = { RetrofitClient.postService.getPostDetail(postId) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                Log.d("PostDetail", "response: $response")
                if (response.isSuccess) {
                    post = response.result  // post 데이터 저장
                    Glide.with(requireContext()).load(post.frontImage).into(binding.ivImageBig)
                    Glide.with(requireContext()).load(post.backImage).into(binding.ivImageSmall)

                    // 비트맵 로드 (색상 추출을 위해)
                    loadBitmapForColorExtraction()
                    
                    setupImageClickListeners()
                }
            },
            onFailure = { t ->
                // 오류 처리
            },
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
        
        // clTagContainer 터치 리스너 설정 (색상 추출용)
        binding.clTagContainer.setOnTouchListener { view, event ->
            if (isColorExtractMode) {
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    originalBitmap?.let { bmp ->
                        val color = getTouchedColor(bmp, event.x, event.y)
                        setIconColor(binding.viewColorIcon, color)
                    }
                }
            }
            true
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
    
    // 비트맵 로드 (색상 추출을 위해)
    private fun loadBitmapForColorExtraction() {
        Glide.with(requireContext())
            .asBitmap()
            .load(post.frontImage)
            .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    originalBitmap = resource
                }
                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
            })
    }
    
    // 아이콘 색상 변경
    private fun setIconColor(view: View, color: Int) {
        val bg = view.background
        if (bg is GradientDrawable) {
            bg.setColor(color)
        } else {
            view.setBackgroundColor(color)
        }
    }
    
    // 이미지에서 색상 추출
    private fun getTouchedColor(bitmap: Bitmap, touchX: Float, touchY: Float): Int {
        val ivWidth = binding.clTagContainer.width
        val ivHeight = binding.clTagContainer.height

        val bmpWidth = bitmap.width
        val bmpHeight = bitmap.height

        val xRatio = touchX / ivWidth
        val yRatio = touchY / ivHeight

        val pixelX = (xRatio * bmpWidth).toInt().coerceIn(0, bmpWidth - 1)
        val pixelY = (yRatio * bmpHeight).toInt().coerceIn(0, bmpHeight - 1)

        return bitmap.getPixel(pixelX, pixelY)
    }
    
    // 해시태그 입력 다이얼로그
    private fun showHashtagDialog(onHashtagSaved: (String) -> Unit) {
        // 다이얼로그 생성
        val dialog = Dialog(requireContext())
        val binding = CustomTagDialogBinding.inflate(layoutInflater)

        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명화

        // 기본값으로 '#' 추가
        binding.etHashtag.setText("#")
        binding.etHashtag.setSelection(1) // 커서를 # 뒤로 이동

        // 취소 버튼
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // 확인 버튼
        binding.btnConfirm.setOnClickListener {
            val input = binding.etHashtag.text.toString().trim()

            // 입력 검증
            if (input.length > 1 && input.startsWith("#")) {
                onHashtagSaved(input)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "올바른 해시태그를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
    
    // 해시태그 TextView 생성 및 Flow에 추가
    private fun createHashtagTextView(text: String, parentLayout: ConstraintLayout, flow: Flow) {
        val textView = TextView(requireContext()).apply {
            id = View.generateViewId()
            this.text = text
            textSize = 16f
            typeface = ResourcesCompat.getFont(requireContext(), R.font.noto_medium)
            includeFontPadding = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            setBackgroundResource(R.drawable.bg_detail_hashtag)
            setPadding(36, 12, 36, 12)

            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }

        parentLayout.addView(textView)
        flow.referencedIds += textView.id
    }
}
