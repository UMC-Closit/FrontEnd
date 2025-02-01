package com.example.umc_closit.Community


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_closit.R
import android.content.Intent

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

//        // 오른쪽 네모 클릭 시 NewChallengeActivity로 이동
//        val rightItem: View = view.findViewById(R.id.right_item)
//        rightItem.setOnClickListener {
//            val intent = Intent(requireContext(), NewChallengeActivity::class.java)
//            startActivity(intent)
//        }
    }
}
