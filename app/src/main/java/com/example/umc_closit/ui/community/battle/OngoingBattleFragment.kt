package com.example.umc_closit.ui.community.battle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.Community.BattlePageAdapter
import com.example.umc_closit.R
import com.example.umc_closit.data.entities.BattleItem
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.battle.BattleListResponse
import com.example.umc_closit.databinding.FragmentCompletedBattleBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OngoingBattleFragment : Fragment() {

    private var _binding: FragmentCompletedBattleBinding? = null
    private val binding get() = _binding!!

    private lateinit var battleAdapter: BattlePageAdapter
    private val battleList = mutableListOf<BattleItem>()

    private var isLoading = false
    private var currentPage = 0
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedBattleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        battleAdapter = BattlePageAdapter(requireContext(), battleList)
        binding.rvCompleted.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCompleted.adapter = battleAdapter

        fetchBattleList(currentPage)

        binding.rvCompleted.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && !isLastPage && lastVisibleItem + 1 >= totalItemCount) {
                    currentPage++
                    fetchBattleList(currentPage)
                }
            }
        })
    }

    private fun fetchBattleList(page: Int) {
        isLoading = true
        val call = RetrofitClient.battleApiService.getBattleList(page, "LATEST", "ACTIVE")
        call.enqueue(object : Callback<BattleListResponse> {
            override fun onResponse(
                call: Call<BattleListResponse>,
                response: Response<BattleListResponse>
            ) {
                isLoading = false
                val battleResponse = response.body()

                if (response.isSuccessful && battleResponse != null && battleResponse.isSuccess) {
                    val battles = battleResponse.result?.battlePreviewList
                    if (page == 0) battleList.clear()

                    if (!battles.isNullOrEmpty()) {
                        binding.tvNobattle.visibility = View.GONE
                        battleList.addAll(battles.map { preview ->
                            BattleItem(
                                id = preview.battleId.toInt(),
                                battleId = preview.battleId,
                                userProfileUrl = preview.firstProfileImage,
                                userName = preview.firstClositId,
                                battleLikeId = 0,
                                leftPostId = preview.firstPostId,
                                rightPostId = preview.secondPostId,
                                leftPostImageUrl = preview.firstPostFrontImage,
                                rightPostImageUrl = preview.secondPostFrontImage
                            )
                        })
                        battleAdapter.notifyDataSetChanged()

                        if (battles.size < 10) isLastPage = true // 페이지 사이즈 10 기준
                    } else {
                        if (page == 0) binding.tvNobattle.visibility = View.VISIBLE
                        isLastPage = true
                    }
                } else {
                    Toast.makeText(requireContext(), "API 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BattleListResponse>, t: Throwable) {
                isLoading = false
                Log.e("API_ERROR", "네트워크 오류: ${t.localizedMessage}")
                Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}