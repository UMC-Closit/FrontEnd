package com.example.umc_closit.ui.community.challenge

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.databinding.FragmentChallengeBinding
import com.example.umc_closit.ui.community.battle.NewBattleActivity

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

        // createButton 클릭 시 NewBattleActivity로 이동
        binding.createButton.setOnClickListener {
            val intent = Intent(requireContext(), NewBattleActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}