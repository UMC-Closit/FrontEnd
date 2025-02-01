package com.example.umc_closit.Community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.umc_closit.databinding.FragmentTodayclosetBinding

class TodayClosetFragment : Fragment() {

    private var _binding: FragmentTodayclosetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayclosetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = listOf("item1", "item2", "item3", "item4") // 더미 데이터
        val adapter = TodayClosetAdapter(itemList)

        binding.recyclerTodaycloset.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerTodaycloset.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
