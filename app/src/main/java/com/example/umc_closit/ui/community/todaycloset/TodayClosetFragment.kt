package com.example.umc_closit.ui.community.todaycloset

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R
import com.example.umc_closit.data.TodayClosetItem
import com.example.umc_closit.data.TodayClosetResponse
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.databinding.FragmentTodayclosetBinding
import com.example.umc_closit.ui.upload.UploadFragment
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

        // RecyclerView (2줄 형태 리스트)
        adapter = TodayClosetAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTodaycloset.layoutManager = layoutManager
        binding.recyclerTodaycloset.adapter = adapter

        // 첫 페이지 데이터 불러오기
        loadTodayClosets(currentPage)

        // 무한 스크롤 리스너 추가
        binding.recyclerTodaycloset.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && hasNext) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                        firstVisibleItemPosition >= 0
                    ) {
                        loadTodayClosets(++currentPage)
                    }
                }
            }
        })

        // createButton 클릭 시 UploadFragment로 이동
        binding.createButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, UploadFragment()) // fragment_upload.xml을 로드하는 Fragment
                addToBackStack(null) // 뒤로 가기 가능하도록 설정
            }
        }
    }

    /**
     * 오늘의 옷장 API 호출
     */
    private fun loadTodayClosets(page: Int) {
        isLoading = true

        RetrofitClient.todayClosetApiService.getTodayClosets(page)
            .enqueue(object : Callback<TodayClosetResponse> {
                override fun onResponse(
                    call: Call<TodayClosetResponse>,
                    response: Response<TodayClosetResponse>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.isSuccess == true) {
                            hasNext = body.result.hasNext
                            if (page == 1) {
                                adapter.submitList(body.result.todayClosets)
                            } else {
                                adapter.addItems(body.result.todayClosets)
                            }
                        } else {
                            Toast.makeText(requireContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TodayClosetResponse>, t: Throwable) {
                    isLoading = false
                    Log.e("TodayCloset", "API 호출 실패", t)
                    Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
