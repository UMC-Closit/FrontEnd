    package com.example.umc_closit.ui.timeline

    import android.app.Activity.RESULT_OK
    import android.content.Context
    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
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

            // SSE 연결 일시적으로 비활성화 (401 에러 해결 후 활성화)
            // startSSEConnection()
            android.util.Log.d("TimelineFragment", "SSE 연결 일시적으로 비활성화됨")

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

                Log.d("TIMELINE_FRAGMENT", "📱 타임라인 아이템 업데이트")
                Log.d("TIMELINE_FRAGMENT", "📱 timelineItems: $timelineItems")
                Log.d("TIMELINE_FRAGMENT", "📱 timelineItems 크기: ${timelineItems?.size ?: 0}")

                if (timelineItems.isNullOrEmpty()) {
                    Log.d("TIMELINE_FRAGMENT", "📱 타임라인 아이템이 비어있음 - 빈 화면 표시")
                    binding.rvTimeline.visibility = View.GONE
                    if (timelineViewModel.isLoading.value != true) {
                        binding.tvPostNone.visibility = View.VISIBLE
                    }
                } else {
                    Log.d("TIMELINE_FRAGMENT", "📱 타임라인 아이템 있음 - 리스트 표시")
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
            // SSE 연결 중지
            NotificationSSEManager.stopSSEConnection()
            android.util.Log.d("TimelineFragment", "onDestroyView - SSE 연결 중지")
            _binding = null
        }

        private fun startSSEConnection() {
            val token = TokenUtils.getAccessToken(requireContext())
            if (token.isNullOrEmpty()) {
                android.util.Log.e("TimelineFragment", "토큰이 없음 - SSE 연결 건너뜀")
            } else {
                android.util.Log.d("TimelineFragment", "SSE 연결 시작 - 토큰: ${token.take(20)}...")
                NotificationSSEManager.startSSEConnection(token) // SSE 연결
            }
        }
    }


