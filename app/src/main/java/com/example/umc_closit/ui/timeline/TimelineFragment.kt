package com.example.umc_closit.ui.timeline

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.databinding.FragmentTimelineBinding
import com.example.umc_closit.model.TimelineViewModel
import com.example.umc_closit.ui.timeline.notification.NotificationActivity
import com.example.umc_closit.ui.timeline.notification.NotificationSSEManager
import com.example.umc_closit.utils.TokenUtils

class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!
    private val timelineViewModel: TimelineViewModel by viewModels()
    private lateinit var timelineAdapter: TimelineAdapter

    private var accessToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기 상태 설정
        binding.progressBar.visibility = View.VISIBLE
        binding.rvTimeline.visibility = View.GONE
        binding.tvPostNone.visibility = View.GONE

        val token = TokenUtils.getAccessToken(requireContext()) ?: return
        NotificationSSEManager.startSSEConnection(token) // SSE 연결

        // Notification 아이콘 클릭 이벤트
        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        // SharedPreferences에서 accessToken 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        accessToken = sharedPreferences.getString("accessToken", "") ?: ""

        timelineAdapter = TimelineAdapter(requireContext(), mutableListOf())

        binding.rvTimeline.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timelineAdapter

            // 스크롤 감지해서 다음 페이지 불러오기
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val itemCount = layoutManager.itemCount

                    if (timelineViewModel.isLoading.value != true && timelineViewModel.hasNextPage && lastVisibleItemPosition == itemCount - 1) {
                        timelineViewModel.fetchTimelinePosts(context = requireContext())
                    }
                }
            })
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.tvPostNone.visibility = View.GONE
            timelineViewModel.resetPage()
            timelineViewModel.fetchTimelinePosts(context = requireContext())
        }


        // 스와이프 새로고침 설정
        timelineViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvPostNone.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        timelineViewModel.timelineItems.observe(viewLifecycleOwner) { timelineItems ->
            binding.swipeRefreshLayout.isRefreshing = false

            if (timelineItems.isNullOrEmpty()) {
                binding.rvTimeline.visibility = View.GONE
                if (timelineViewModel.isLoading.value != true) {
                    binding.tvPostNone.visibility = View.VISIBLE
                }
            } else {
                binding.rvTimeline.visibility = View.VISIBLE
                binding.tvPostNone.visibility = View.GONE
                timelineAdapter.updateTimelineItems(timelineItems)
            }
        }


        timelineViewModel.timelineItems.observe(viewLifecycleOwner) { timelineItems ->
            binding.swipeRefreshLayout.isRefreshing = false

            if (!timelineViewModel.isLoading.value!! && timelineItems != null) {
                // 로딩 끝난 후에만 데이터 업데이트
                timelineAdapter.updateTimelineItems(timelineItems)

                if (timelineItems.isEmpty()) {
                    binding.tvPostNone.visibility = View.VISIBLE
                } else {
                    binding.tvPostNone.visibility = View.GONE
                }
            }
        }
        // 초기 데이터 로드
        timelineViewModel.fetchTimelinePosts(context = requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationSSEManager.stopSSEConnection()
        _binding = null
    }
}
