package com.example.umc_closit.ui.community

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.umc_closit.R
import com.example.umc_closit.databinding.FragmentCommunityBinding
import com.example.umc_closit.ui.community.battle.BattleFragment
import com.example.umc_closit.ui.community.battle.NewBattleActivity
import com.example.umc_closit.ui.community.challenge.ChallengeFragment
import com.example.umc_closit.ui.community.todaycloset.TodayClosetFragment
import com.example.umc_closit.ui.timeline.detail.DetailActivity
import com.example.umc_closit.ui.upload.UploadActivity

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "오늘의 옷장" 버튼 클릭 시 fragment_todaycloset으로 변경
        binding.btnTodaycloset.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, TodayClosetFragment()) // fragment_todaycloset.xml을 로드하는 Fragment
            fragmentTransaction.addToBackStack(null) // 뒤로 가기 가능하도록 설정
            fragmentTransaction.commit()
        }

        // imgDetail1 버튼 클릭 시 DetailActivity 실행
        binding.imgDetail1.setOnClickListener {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            startActivity(intent)
        }

        binding.imgDetail2.setOnClickListener {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            startActivity(intent)
        }

        // btnUpload 버튼 클릭 시 UploadActivity 실행
        binding.btnUpload.setOnClickListener {
            val intent = Intent(requireContext(), UploadActivity::class.java)
            startActivity(intent)
        }


        binding.btnBattle.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, BattleFragment()) // fragment_battle.xml을 로드하는 Fragment
            fragmentTransaction.addToBackStack(null) // 뒤로 가기 가능하도록 설정
            fragmentTransaction.commit()
        }
        // btn_upload2 버튼 클릭 시 NewBattleActivity 실행
        binding.btnUpload2.setOnClickListener {
            val intent = Intent(requireContext(), NewBattleActivity::class.java)
            startActivity(intent)
        }
        // gotochallenge 터치 시 ChallengeFragment 실행
        binding.gotochallenge.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, ChallengeFragment()) // fragment_todaycloset.xml을 로드하는 Fragment
            fragmentTransaction.addToBackStack(null) // 뒤로 가기 가능하도록 설정
            fragmentTransaction.commit()
        }

        binding.imgBattle1.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, ChallengeFragment()) // fragment_todaycloset.xml을 로드하는 Fragment
            fragmentTransaction.addToBackStack(null) // 뒤로 가기 가능하도록 설정
            fragmentTransaction.commit()
        }

        binding.imgBattle2.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, ChallengeFragment()) // fragment_todaycloset.xml을 로드하는 Fragment
            fragmentTransaction.addToBackStack(null) // 뒤로 가기 가능하도록 설정
            fragmentTransaction.commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
