package com.example.umc_closit.ui.community.todaycloset

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R
import com.example.umc_closit.data.TodayClosetItem
import com.example.umc_closit.data.TodayClosetResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.databinding.FragmentTodayclosetBinding
import com.example.umc_closit.ui.upload.UploadFragment
import com.example.umc_closit.utils.TokenUtils

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodayClosetFragment : Fragment() {

    private var _binding: FragmentTodayclosetBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TodayClosetAdapter

    // 페이징 변수
    private var currentPage = 1
    private var isLoading = false
    private var hasNext = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayclosetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기화
        adapter = TodayClosetAdapter()

        binding.recyclerTodaycloset.adapter = adapter
        binding.recyclerTodaycloset.layoutManager = GridLayoutManager(requireContext(), 2)

        // API 호출
        loadTodayClosets(currentPage)

        // 스크롤 페이징 처리
        binding.recyclerTodaycloset.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem == adapter.itemCount - 1 && hasNext && !isLoading) {
                    loadTodayClosets(currentPage + 1)
                }
            }
        })

        // 첫 페이지 데이터 불러오기
        loadTodayClosets(currentPage)


        // createButton 클릭 시 UploadFragment로 이동
        binding.createButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, UploadFragment())
                addToBackStack(null)
            }
        }
    }

    /**
     * 오늘의 옷장 API 호출
     */
    private fun loadTodayClosets(page: Int) {
        isLoading = true
        val authToken = "Bearer ${TokenUtils.getAccessToken(requireContext())}"

        TokenUtils.handleTokenRefresh(
            call = RetrofitClient.todayClosetApiService.getTodayClosets(authToken, page),
            onSuccess = { response ->
                isLoading = false
                if (response.isSuccess) {
                    hasNext = response.result.hasNext
                    currentPage = page
                    if (page == 1) {
                        adapter.submitList(response.result.todayClosets)
                    } else {
                        adapter.addItems(response.result.todayClosets)
                    }
                } else {
                    Toast.makeText(requireContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { throwable ->
                isLoading = false
                Log.e("TodayCloset", "API 호출 실패", throwable)
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            },
            retryCall = {
                val newAuthToken = "Bearer ${TokenUtils.getAccessToken(requireContext())}"
                RetrofitClient.todayClosetApiService.getTodayClosets(newAuthToken, page)
            },
            context = requireContext()
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
