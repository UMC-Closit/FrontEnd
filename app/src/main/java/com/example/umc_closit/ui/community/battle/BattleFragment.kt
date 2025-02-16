package com.example.umc_closit.ui.community.battle

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.Community.BattlePageAdapter
import com.example.umc_closit.R
import com.example.umc_closit.data.entities.BattleItem
import com.example.umc_closit.ui.community.challenge.ChallengeFragment

class BattleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.Battle_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 더미 데이터 추가 (BattleItem 리스트)
        val itemList = List(10) { index ->
            BattleItem(
                id = index.toLong(),
                battleId = (1000 + index).toLong(),
                userProfileUrl = (2000 + index).toString(),
                userName = (3000 + index).toString(),
                battleLikeId = (4000 + index).toLong(),
                leftPostId = (5000 + index).toLong(),
                rightPostId = (6000 + index).toLong()
            )
        }

        // RecyclerView에 BattlePageAdapter 연결
        recyclerView.adapter = BattlePageAdapter(requireContext(), itemList.toMutableList())

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
}
