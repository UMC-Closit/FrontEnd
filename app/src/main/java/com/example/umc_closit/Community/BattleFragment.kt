package com.example.umc_closit.Community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

class BattleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.Battle_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        // 더미 데이터 추가
        val itemList = List(10) { "아이템 ${it + 1}" }
        recyclerView.adapter = BattlePageAdapter(itemList)

        // 왼쪽 네모 클릭 시 ChallengeFragment로 이동
        val leftItem: View = view.findViewById(R.id.left_item)
        leftItem.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, ChallengeFragment()) // fragment_challenge.xml을 로드하는 Fragment
            fragmentTransaction.addToBackStack(null) // 뒤로 가기 가능하도록 설정
            fragmentTransaction.commit()
        }
    }
}
