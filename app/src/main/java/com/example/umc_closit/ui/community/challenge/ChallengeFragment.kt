package com.example.umc_closit.ui.community.challenge

import ChallengeApiService
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.data.remote.challenge.*
import com.example.umc_closit.databinding.FragmentChallengeBinding
import com.example.umc_closit.ui.community.battle.NewBattleActivity
import com.example.umc_closit.utils.TokenUtils
import com.example.umc_closit.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ChallengeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 더미 데이터 추가
        val itemList = List(10) { "아이템 ${it + 1}" }
        binding.ChallengeRecyclerView.adapter = ChallengeAdapter(itemList, requireContext())

        // createButton 클릭 시 배틀 챌린지 API 호출
        binding.createButton.setOnClickListener {
            uploadChallenge(battleId = 1, postId = 123)
        }
    }

    /**
     * 배틀 챌린지 API 호출 메서드 (토큰 재발급 로직 포함)
     */
    private fun uploadChallenge(battleId: Int, postId: Int) {
        val apiService = RetrofitClient.createService(ChallengeApiService::class.java)

        // 원래 호출 정의
        val originalCall = {
            apiService.uploadChallenge(
                token = "Bearer ${TokenUtils.getAccessToken(requireContext())}",
                battleId = battleId,
                requestBody = ChallengeRequest(postId = postId)
            )
        }

        // TokenUtils를 통한 호출 처리
        TokenUtils.handleTokenRefresh(
            call = originalCall(),
            onSuccess = { response ->
                val result = response as ChallengeResponse
                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "배틀 챌린지 신청 성공!", Toast.LENGTH_SHORT).show()
                    println("✅ 배틀 챌린지 성공: ${result.result}")
                    // 성공 시 NewBattleActivity로 이동
                    val intent = Intent(requireContext(), NewBattleActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "실패: ${result.message}", Toast.LENGTH_SHORT).show()
                    println("실패: ${result.message}")
                }
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "요청 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                println("요청 실패: ${error.message}")
            },
            retryCall = originalCall, // 재발급 후 다시 시도할 API
            context = requireContext()
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
