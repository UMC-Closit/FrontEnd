package com.example.umc_closit.Community


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R

class ChallengeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_challenge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.Challenge_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 더미 데이터 추가
        val itemList = List(10) { "아이템 ${it + 1}" }
        recyclerView.adapter = ChallengeAdapter(itemList)
    }
}
