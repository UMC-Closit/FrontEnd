package com.example.umc_closit.ui.community

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.R
import com.example.umc_closit.databinding.FragmentCommunityBinding
import com.example.umc_closit.ui.community.adapter.BattlePostItem // 어댑터에서 정의한 데이터 클래스 임포트
import com.example.umc_closit.ui.community.adapter.CommunityBattlePostAdapter
import com.example.umc_closit.ui.community.adapter.CommunityTodayClosetAdapter
import com.example.umc_closit.ui.community.adapter.TodayClosetItem // 어댑터에서 정의한 데이터 클래스 임포트
import com.example.umc_closit.ui.community.battle.BattleFragment
import com.example.umc_closit.ui.community.battle.NewBattleActivity
import com.example.umc_closit.ui.community.challenge.ChallengeFragment
import com.example.umc_closit.ui.community.todaycloset.TodayClosetFragment
import com.example.umc_closit.ui.timeline.detail.DetailActivity
import com.example.umc_closit.ui.upload.UploadActivity

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private lateinit var todayClosetAdapter: CommunityTodayClosetAdapter
    private lateinit var battlePostAdapter: CommunityBattlePostAdapter

    // postId와 battleId는 각 아이템을 식별할 수 있는 고유한 값이어야 합니다.
    private val todayClosetSampleData = listOf(
        TodayClosetItem(R.drawable.ic_insta, "today_post_1"), // 실제 드로어블 및 ID로 변경
        TodayClosetItem(R.drawable.ic_insta, "today_post_2"),
        TodayClosetItem(R.drawable.ic_insta, "today_post_3")
    )

    private val battlePostSampleData = listOf(
        BattlePostItem(R.drawable.ic_insta, "battle_1"), // 실제 드로어블 및 ID로 변경
        BattlePostItem(R.drawable.ic_insta, "battle_2"),
        BattlePostItem(R.drawable.ic_insta, "battle_3")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupTodayClosetRecyclerView()
        setupBattlePostRecyclerView()
    }

    private fun setupClickListeners() {
        // "오늘의 옷장" 타이틀 TextView 클릭 시 TodayClosetFragment로 이동
        binding.tvTodayClosetTitle.setOnClickListener {
            navigateTo(TodayClosetFragment())
        }

        // "오늘의 옷장" 업로드 버튼 클릭 시 UploadActivity 실행
        binding.btnUploadTodayCloset.setOnClickListener {
            startActivity(Intent(requireContext(), UploadActivity::class.java))
        }

        // "배틀 게시판" 타이틀 TextView 클릭 시 BattleFragment로 이동
        binding.tvBattlePostTitle.setOnClickListener {
            navigateTo(BattleFragment())
        }

        // "배틀 게시판" 업로드 버튼 클릭 시 NewBattleActivity 실행
        binding.btnUploadBattlePost.setOnClickListener {
            startActivity(Intent(requireContext(), NewBattleActivity::class.java))
        }
    }

    private fun setupTodayClosetRecyclerView() {
        todayClosetAdapter = CommunityTodayClosetAdapter(todayClosetSampleData) { clickedItem ->
            // "오늘의 옷장" RecyclerView 아이템 클릭 시 DetailActivity로 이동
            val intent = Intent(requireContext(), DetailActivity::class.java)
            // DetailActivity에 어떤 아이템인지 정보를 전달할 수 있습니다.
            intent.putExtra("POST_ID", clickedItem.postId) // 예시: postId 전달
            // 필요하다면 다른 정보도 전달 (예: clickedItem.imageResId 등)
            startActivity(intent)
        }
        binding.rvCommunityTodayCloset.apply {
            adapter = todayClosetAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupBattlePostRecyclerView() {
        battlePostAdapter = CommunityBattlePostAdapter(battlePostSampleData) { clickedItem ->
            // "배틀 게시판" RecyclerView 아이템 클릭 시 ChallengeFragment로 이동
            // (요구사항에 따라 DetailActivity 또는 다른 Fragment/Activity로 변경 가능)
            // 여기서는 ChallengeFragment로 이동하는 것으로 가정합니다.
            val fragment = ChallengeFragment().apply {
                arguments = Bundle().apply {
                    // ChallengeFragment에 어떤 배틀 아이템인지 정보를 전달할 수 있습니다.
                    putString("BATTLE_ID", clickedItem.battleId) // 예시: battleId 전달
                }
            }
            navigateTo(fragment)
        }
        binding.rvCommunityBattlePost.apply {
            adapter = battlePostAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // fragment_container는 Activity 레이아웃에 있는 Fragment를 담는 컨테이너 ID여야 합니다.
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // RecyclerView 어댑터를 null로 설정하여 메모리 누수 방지 (선택 사항이지만 권장)
        binding.rvCommunityTodayCloset.adapter = null
        binding.rvCommunityBattlePost.adapter = null
        _binding = null
    }
}