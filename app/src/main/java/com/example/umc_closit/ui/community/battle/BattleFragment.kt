package com.example.umc_closit.ui.community.battle

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.battle.BattleApiService
import com.example.umc_closit.data.remote.battle.ChallengeBattlePreview
import com.example.umc_closit.data.remote.battle.ChallengeBattleResponse
import com.example.umc_closit.databinding.FragmentBattleBinding
import com.example.umc_closit.ui.community.challenge.ChallengeFragment
import com.example.umc_closit.utils.TokenUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BattleFragment : Fragment() {

    private var _binding: FragmentBattleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBattleBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = BattlePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val tabText = if (position == 0) "진행중" else "완료됨"
            val textView = TextView(requireContext()).apply {
                text = tabText
                textSize = 12f
                includeFontPadding = false
                setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                typeface = Typeface.DEFAULT
                gravity = Gravity.BOTTOM
                setPadding(0, 0, 0, 0) // ✅ 패딩 제거
                minHeight = 0 // ✅ 최소 높이 제거
                setLineSpacing(0f, 1f) // ✅ 줄 간격 줄이기
            }
            tab.customView = textView
        }.attach()

        binding.clChallengeWrapper.setOnClickListener{
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, ChallengeFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.clChallenge.setOnClickListener {
            val intent = Intent(requireContext(), NewBattleActivity::class.java)
            startActivity(intent)
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val textView = tab.customView as? TextView
                textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                textView?.setTypeface(null, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val textView = tab.customView as? TextView
                textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                textView?.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // 처음 시작 시 첫 번째 탭에 스타일 적용
        val firstTab = binding.tabLayout.getTabAt(0)
        val firstTextView = firstTab?.customView as? TextView
        firstTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        firstTextView?.setTypeface(null, Typeface.BOLD)

        // 챌린지 데이터 불러오기
        fetchChallengeBattles()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchChallengeBattles() {
        val apiService = RetrofitClient.createService(BattleApiService::class.java)

        TokenUtils.handleTokenRefresh(
            call = apiService.getChallengeBattles(page = 0),
            onSuccess = { response ->
                val result = response as ChallengeBattleResponse
                if (result.isSuccess && result.result != null) {
                    val challengeList = result.result.challengeBattlePreviewList
                    Log.d("ChallengeList", "받은 리스트 크기: ${challengeList.size}")
                    challengeList.forEach {
                        Log.d("ChallengeList", "title=${it.title}, id=${it.battleId}")
                    }
                    val adapter = ChallengePreviewAdapter(challengeList, requireContext())
                    binding.rvChallenge.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.rvChallenge.adapter = adapter
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
}