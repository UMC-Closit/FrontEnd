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
import com.example.umc_closit.ui.community.challenge.ChallengeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BattleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var battleAdapter: BattlePageAdapter
    private val battleList = mutableListOf<BattleItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        recyclerView = view.findViewById(R.id.Battle_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        battleAdapter = BattlePageAdapter(requireContext(), battleList)
        recyclerView.adapter = battleAdapter

        // 배틀 리스트 API 호출
        fetchBattleList(0)
        // 왼쪽 네모 클릭 시 ChallengeFragment로 이동
        val leftItem: View = view.findViewById(R.id.left_item)
        leftItem.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ChallengeFragment())
                .addToBackStack(null)
                .commit()
        }

        // 오른쪽 네모 클릭 시 ChallengeFragment로 이동
        val rightItem: View = view.findViewById(R.id.right_item)
        rightItem.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ChallengeFragment())
                .addToBackStack(null)
                .commit()
        }

        // createButton 클릭 시 NewBattleActivity로 이동
        val createButton: View = view.findViewById(R.id.createButton)
        createButton.setOnClickListener {
            val intent = Intent(requireContext(), NewBattleActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 배틀 리스트 API 호출
     */
    private fun fetchBattleList(page: Int) {
        val call = RetrofitClient.battleApiService.getBattleList(page)
        call.enqueue(object : Callback<BattleListResponse> {
            override fun onResponse(
                call: Call<BattleListResponse>,
                response: Response<BattleListResponse>
            ) {
                if (response.isSuccessful) {
                    val battleResponse = response.body()
                    if (battleResponse != null && battleResponse.isSuccess) {
                        val battles = battleResponse.result?.battlePreviewList
                        battleList.clear()
                        if (battles != null) {
                            battleList.addAll(battles.map { preview ->
                                BattleItem(
                                    id = preview.battleId.toInt(),
                                    battleId = preview.battleId,
                                    userProfileUrl = preview.firstProfileImage,
                                    userName = preview.firstClositId,
                                    battleLikeId = 0, // API에서 제공되지 않으면 기본값
                                    leftPostId = preview.firstPostId,
                                    rightPostId = preview.secondPostId,
                                    leftPostImageUrl = preview.firstPostFrontImage,  // 추가
                                    rightPostImageUrl = preview.secondPostFrontImage // 추가
                                )
                            })
                        }
                        battleAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "API 실패: ${battleResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API_ERROR", "응답 실패: ${response.code()} - ${response.message()}")
                    Toast.makeText(requireContext(), "불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BattleListResponse>, t: Throwable) {
                Log.e("API_ERROR", "네트워크 오류: ${t.localizedMessage}")
                Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
