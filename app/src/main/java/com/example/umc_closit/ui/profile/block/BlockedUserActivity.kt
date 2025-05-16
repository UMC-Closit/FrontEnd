package com.example.umc_closit.ui.profile.block

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.profile.BlockRequest
import com.example.umc_closit.data.remote.profile.BlockedUser
import com.example.umc_closit.databinding.ActivityBlockedUserBinding
import com.example.umc_closit.utils.TokenUtils

class BlockedUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlockedUserBinding
    private lateinit var adapter: BlockedUserAdapter
    private val blockedUsers = mutableListOf<BlockedUser>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockedUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = BlockedUserAdapter(blockedUsers) { user ->
            unblockUser(user)
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.recyclerViewNew.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNew.adapter = adapter

        loadBlockedUsers()
    }

    private fun loadBlockedUsers() {
        val apiCall = { RetrofitClient.profileService.getBlockedUsers() }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    blockedUsers.clear()
                    blockedUsers.addAll(response.result.blockedUsers)
                    adapter.notifyDataSetChanged()

                    binding.tvBlockNone.visibility =
                        if (blockedUsers.isEmpty()) View.VISIBLE else View.GONE
                }
            },
            onFailure = { t ->
                Log.e("BlockedUser", "차단 목록 불러오기 실패: ${t.message}")
            },
            retryCall = apiCall,
            context = this
        )
    }

    private fun unblockUser(user: BlockedUser) {
        val apiCall = { RetrofitClient.profileService.unblockUser(BlockRequest(user.clositId)) }

        TokenUtils.handleTokenRefresh(
            call = apiCall(),
            onSuccess = { response ->
                if (response.isSuccess) {
                    Toast.makeText(this, "차단 해제 완료", Toast.LENGTH_SHORT).show()
                    blockedUsers.remove(user)
                    adapter.notifyDataSetChanged()
                    binding.tvBlockNone.visibility = if (blockedUsers.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(this, "차단 해제 실패: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { t ->
                Toast.makeText(this, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            },
            retryCall = apiCall,
            context = this
        )
    }
}