package com.example.umc_closit.ui.timeline

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.R
import com.example.umc_closit.databinding.FragmentTimelineBinding
import com.example.umc_closit.model.TimelineViewModel
import com.example.umc_closit.data.TimelineItem
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

        // Notification 아이콘 클릭 이벤트
        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        // RecyclerView 설정
        timelineAdapter = TimelineAdapter(requireContext(), mutableListOf(), mutableListOf())
        binding.rvTimeline.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timelineAdapter
        }

        // LiveData 관찰
        timelineViewModel.timelineItems.observe(viewLifecycleOwner, Observer { timelineItems ->
            timelineAdapter.updateTimelineItems(timelineItems)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
