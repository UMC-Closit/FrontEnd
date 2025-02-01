package com.example.umc_closit.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.R
import com.example.umc_closit.data.HighlightItem
import com.example.umc_closit.data.RecentItem
import com.example.umc_closit.databinding.FragmentProfileBinding
import com.example.umc_closit.ui.profile.highlight.HighlightAdapter
import com.example.umc_closit.ui.profile.history.HistoryActivity
import com.example.umc_closit.ui.profile.recent.RecentAdapter
import com.example.umc_closit.utils.DateUtils.getCurrentDate

class ProfileFragment : Fragment() {

    // ViewBinding 선언
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
        highlightAdapter = HighlightAdapter(highlightItems, {
            // "+" 버튼 클릭 시 새로운 하이라이트 추가
            val newHighlight = HighlightItem(
                R.drawable.img_profile_highlight,
                getCurrentDate()
            )
            highlightAdapter.updateItems(newHighlight)
        }, screenWidth) // 화면 너비 전달

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
    }

}
