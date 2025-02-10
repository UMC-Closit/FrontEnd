package com.example.umc_closit.ui.upload

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.umc_closit.R
import com.example.umc_closit.databinding.FragmentUploadBinding
import com.example.umc_closit.model.TimelineViewModel
import com.example.umc_closit.ui.mission.MissionActivity

class UploadFragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding
    private val timelineViewModel: TimelineViewModel by viewModels() // ✅ ViewModel 연결
    private val photoList = mutableListOf<Pair<Int, Int>>() // ✅ mainImageResId와 overlayImageResId 저장
    private lateinit var uploadAdapter: UploadAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeTimelineData() // ✅ ViewModel 데이터 감지
        setupUploadButton()
    }

    private fun observeTimelineData() {
        timelineViewModel.timelineItems.observe(viewLifecycleOwner) { timelineItems ->
            photoList.clear()
            timelineItems.forEach { item ->
                photoList.add(Pair(item.mainImageResId, item.overlayImageResId))
            }
            setupViewPager()
        }
    }

    private fun setupViewPager() {
        uploadAdapter = UploadAdapter(photoList)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val itemHeight = (screenHeight * 0.9).toInt() // ✅ 아이템 높이를 화면 높이의 90%로 설정
        val pageMarginPx = (screenWidth * 0.06).toInt() // ✅ 좌우 여백을 화면 너비의 5%로 설정
        val offsetPx = pageMarginPx * 1 // ✅ 미리보기 효과를 위한 간격

        binding.photoViewPager.apply {
            adapter = uploadAdapter
            offscreenPageLimit = 3
            clipToPadding = false
            clipChildren = false
            setPadding(pageMarginPx, 0, pageMarginPx, 0) // ✅ 좌우 패딩을 설정하여 미리보기가 유지되도록 함
        }

        binding.photoViewPager.setPageTransformer { page, position ->
            val pageTranslationX = -offsetPx * position
            page.translationX = pageTranslationX
            page.layoutParams.height = itemHeight // ✅ 아이템 높이 적용
        }
    }

    private fun setupUploadButton() {
        binding.btnUpload.setOnClickListener {
            if (photoList.isNotEmpty()) {
                Toast.makeText(requireContext(), "사진이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                // MissionActivity로 이동
                val intent = Intent(requireContext(), MissionActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "업로드할 사진을 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}