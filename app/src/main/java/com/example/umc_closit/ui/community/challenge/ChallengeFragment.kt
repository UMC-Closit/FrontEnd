package com.example.umc_closit.ui.community.challenge

import ChallengeApiService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.data.remote.challenge.*
import com.example.umc_closit.databinding.FragmentChallengeBinding
import com.example.umc_closit.utils.TokenUtils
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.battle.ChallengeBattleResponse
import com.example.umc_closit.ui.community.battle.NewBattleActivity
import com.example.umc_closit.data.remote.battle.BattleApiService
import com.example.umc_closit.data.remote.battle.ChallengeBattlePreview
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!
    private lateinit var challengeAdapter: ChallengeAdapter
    private val challengeList = mutableListOf<ChallengeBattlePreview>() // 실제 데이터 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기화
        binding.ChallengeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        challengeAdapter = ChallengeAdapter(challengeList, requireContext())
        binding.ChallengeRecyclerView.adapter = challengeAdapter

        // 챌린지 데이터 불러오기
        fetchChallengeBattles()

        // createButton 클릭 시 배틀 챌린지 API 호출 (더미 데이터로)
        binding.createButton.setOnClickListener {
            val intent = Intent(requireContext(), NewBattleActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 🔥 챌린지 목록 API 호출
     */
    private fun fetchChallengeBattles() {
        val apiService = RetrofitClient.createService(BattleApiService::class.java)

        TokenUtils.handleTokenRefresh(
            call = apiService.getChallengeBattles(page = 0),
            onSuccess = { response ->
                val result = response as ChallengeBattleResponse
                if (result.isSuccess && result.result != null) {
                    val challengeList = result.result.challengeBattlePreviewList
                    val adapter = ChallengeAdapter(challengeList, requireContext())
                    binding.ChallengeRecyclerView.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "불러오기 실패: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), "네트워크 오류: ${error.message}", Toast.LENGTH_SHORT).show()
            },
            context = requireContext()
        )
    }

    /**
     * 🔥 배틀 챌린지 API 호출
     */
    private fun uploadChallenge(battleId: Int, postId: Int) {
        val apiService = RetrofitClient.createService(ChallengeApiService::class.java)

        val originalCall = {
            apiService.uploadChallenge(
                token = "Bearer ${TokenUtils.getAccessToken(requireContext())}",
                battleId = battleId,
                requestBody = ChallengeRequest(postId = postId)
            )
        }

        TokenUtils.handleTokenRefresh(
            call = originalCall(),
            onSuccess = { response ->
                val result = response as ChallengeResponse
                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "배틀 챌린지 신청 성공!", Toast.LENGTH_SHORT).show()
                    println("✅ 배틀 챌린지 성공: ${result.result}")
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
            context = requireContext()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
