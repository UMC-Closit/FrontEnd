package com.example.umc_closit.ui.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.umc_closit.data.remote.RetrofitClient
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.R
import com.example.umc_closit.databinding.FragmentCommunityBinding
import com.example.umc_closit.data.remote.battle.TodayClosetItem
import com.example.umc_closit.ui.community.adapter.CommunityBattlePostAdapter
import com.example.umc_closit.ui.community.adapter.CommunityTodayClosetAdapter
import com.example.umc_closit.data.remote.battle.TodayClosetResponse
import com.example.umc_closit.data.remote.battle.TodayClosetUploadResponse
import com.example.umc_closit.data.remote.post.HashtagSearchResponse
import com.example.umc_closit.ui.community.battle.BattleFragment
import com.example.umc_closit.ui.community.battle.NewBattleActivity
import com.example.umc_closit.ui.community.challenge.ChallengeFragment
import com.example.umc_closit.ui.community.todaycloset.TodayClosetFragment
import com.example.umc_closit.ui.timeline.detail.DetailActivity
import com.example.umc_closit.ui.upload.UploadActivity
import com.example.umc_closit.data.remote.battle.BattleListResponse
import com.example.umc_closit.data.remote.battle.BattlePreview
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private lateinit var todayClosetAdapter: CommunityTodayClosetAdapter
    private lateinit var battlePostAdapter: CommunityBattlePostAdapter

//    private val todayClosetSampleData = listOf(
//        TodayClosetItem(R.drawable.ic_insta, 1), // 실제 드로어블 및 ID로 변경
//        TodayClosetItem(R.drawable.ic_insta, 2),
//        TodayClosetItem(R.drawable.ic_insta, 3)
//    )
//
//    private val battlePostSampleData = listOf(
//        BattleItem(R.drawable.ic_insta, "battle_1"), // 실제 드로어블 및 ID로 변경
//        BattleItem(R.drawable.ic_insta, "battle_2"),
//        BattleItem(R.drawable.ic_insta, "battle_3")
//    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("CommunityFragment", "onCreateView 호출됨")
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CommunityFragment", "onViewCreated 호출됨")

        setupClickListeners()
        setupTodayClosetRecyclerView()
        setupBattlePostRecyclerView()
        setupSearchFunctionality()
        // RecyclerView 설정 완료 후 API 호출
        Log.d("CommunityFragment", "API 호출 직전")
        TodayClosetApiService()
        BattlePostApiService()
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
        todayClosetAdapter = CommunityTodayClosetAdapter(emptyList()) { clickedItem ->
            // "오늘의 옷장" RecyclerView 아이템 클릭 시 DetailActivity로 이동
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("POST_ID", clickedItem.postId) // 예시: postId 전달
            startActivity(intent)
        }
        binding.rvCommunityTodayCloset.apply {
            adapter = todayClosetAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupBattlePostRecyclerView() {
        battlePostAdapter = CommunityBattlePostAdapter(emptyList()) { clickedItem ->
            // "배틀 게시판" RecyclerView 아이템 클릭 시 ChallengeFragment로 이동
            val fragment = ChallengeFragment().apply {
                arguments = Bundle().apply {
                    // ChallengeFragment에 어떤 배틀 아이템인지 정보를 전달할 수 있습니다.
//                    putString("BATTLE_ID", clickedItem.battleId) // 예시: battleId 전달
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
        binding.rvCommunityTodayCloset.adapter = null
        _binding = null
    }

    private fun TodayClosetApiService() {
        Log.d("TodayCloset", "API 호출 시작")
        try {
            Log.d("TodayCloset", "RetrofitClient 접근 시도")
            RetrofitClient.todayClosetApiService.getTodayClosets(page = 0)
            .enqueue(object : Callback<TodayClosetResponse> {
                override fun onResponse(
                    call: Call<TodayClosetResponse>,
                    response: Response<TodayClosetResponse>
                ) {
                    Log.d("TodayCloset", "API 응답 받음: ${response.isSuccessful}")
                    Log.d("TodayCloset", "응답 바디: ${response.body()}")
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val items = response.body()?.result?.todayClosets ?: emptyList()
                        Log.d("TodayCloset", "받은 아이템 개수: ${items.size}")
                        Log.d("TodayCloset", "result 전체: ${response.body()?.result}")
                        // 첫 번째 아이템의 이미지 URL 로그 출력
                        if (items.isNotEmpty()) {
                            val firstItem = items[0]
                            Log.d("TodayCloset", "첫 번째 아이템 - frontImage: ${firstItem.frontImage}")
                            Log.d("TodayCloset", "첫 번째 아이템 - profileImage: ${firstItem.profileImage}")
                        }
                        todayClosetAdapter.updateData(items)
                    } else {
                        Log.e("TodayCloset", "API 응답 실패: ${response.body()?.message}")
                    }
                }

                override fun onFailure(call: Call<TodayClosetResponse>, t: Throwable) {
                    Log.e("TodayCloset", "네트워크 오류: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("TodayCloset", "RetrofitClient 접근 오류: ${e.message}")
        }
    }

    private fun BattlePostApiService() {
        Log.d("BattlePost", "배틀 게시판 API 호출 시작")
        try {
            RetrofitClient.battleApiService.getBattleList(page = 0, sorting = "LATEST", status = "ACTIVE")
            .enqueue(object : Callback<BattleListResponse> {
                override fun onResponse(
                    call: Call<BattleListResponse>,
                    response: Response<BattleListResponse>
                ) {
                    Log.d("BattlePost", "API 응답 받음: ${response.isSuccessful}")
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val battleList = response.body()?.result?.battlePreviewList ?: emptyList()
                        Log.d("BattlePost", "받은 배틀 아이템 개수: ${battleList.size}")
                        
                        if (battleList.isNotEmpty()) {
                            val firstBattle = battleList[0]
                            Log.d("BattlePost", "첫 번째 배틀 - firstPostFrontImage: ${firstBattle.firstPostFrontImage}")
                            Log.d("BattlePost", "첫 번째 배틀 - firstClositId: ${firstBattle.firstClositId}")
                            Log.d("BattlePost", "첫 번째 배틀 - likeCount: ${firstBattle.likeCount}")
                        }
                        
                        battlePostAdapter.updateData(battleList)
                    } else {
                        Log.e("BattlePost", "API 응답 실패: ${response.body()?.message}")
                    }
                }

                override fun onFailure(call: Call<BattleListResponse>, t: Throwable) {
                    Log.e("BattlePost", "네트워크 오류: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("BattlePost", "RetrofitClient 접근 오류: ${e.message}")
        }
    }

    private fun setupSearchFunctionality() {
        binding.imgSearchIconCommunity.setOnClickListener {
            performHashtagSearch()
        }
        binding.etSearchCommunity.setOnEditorActionListener { v, actionId, event ->
            performHashtagSearch()
            true
        }
    }

    private fun performHashtagSearch() {
        val query = binding.etSearchCommunity.text.toString().trim()
        if (query.isEmpty()) return
        val hashtag = if (query.startsWith("#")) query.substring(1) else query
        RetrofitClient.postService.searchPostsByHashtag(hashtag, 0)
            .enqueue(object : Callback<HashtagSearchResponse> {
                override fun onResponse(
                    call: Call<HashtagSearchResponse>,
                    response: Response<HashtagSearchResponse>
                ) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val items = response.body()?.result?.postPreviewList ?: emptyList()
                        val todayClosetItems = items.map {
                            TodayClosetItem(
                                postId = it.postId,
                                frontImage = it.frontImage,
                                profileImage = it.profileImage,
                                viewCount = 0,
                                todayClosetId = 0,
                                backImage = it.backImage
                            )
                        }
                        todayClosetAdapter.updateData(todayClosetItems)
                    } else {
                        Log.e("HashtagSearch", "API 응답 실패: ${response.body()?.message}")
                    }
                }
                override fun onFailure(call: Call<HashtagSearchResponse>, t: Throwable) {
                    Log.e("HashtagSearch", "네트워크 오류: ${t.message}")
                }
            })
    }
}