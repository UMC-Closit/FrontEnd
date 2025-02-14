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
import com.example.umc_closit.databinding.FragmentTimelineBinding
import com.example.umc_closit.model.TimelineViewModel
import com.example.umc_closit.ui.timeline.notification.NotificationActivity

class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!
    private val timelineViewModel: TimelineViewModel by viewModels()
    private lateinit var timelineAdapter: TimelineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔔 Notification 아이콘 클릭 이벤트
        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        // ✅ SharedPreferences에서 accessToken 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("accessToken", "") ?: ""
        val userId = sharedPreferences.getInt("userId", -1) // 기본값을 -1로 설정

        // 🔥 RecyclerView 설정
        val timelineAdapter = TimelineAdapter(requireContext(), mutableListOf(), accessToken, userId)
        binding.rvTimeline.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timelineAdapter
        }

        timelineViewModel.fetchTimelinePosts(accessToken, userId, context = requireContext())


        // ✅ LiveData 관찰하여 RecyclerView 업데이트
        timelineViewModel.timelineItems.observe(viewLifecycleOwner, Observer { timelineItems ->
            if (timelineItems != null) {
                timelineAdapter.updateTimelineItems(timelineItems)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
