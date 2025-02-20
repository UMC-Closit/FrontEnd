package com.example.umc_closit.ui.profile.highlight

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PostDetailPagerAdapter(
    activity: AppCompatActivity,
    private val postIdList: List<Int>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = postIdList.size

    override fun createFragment(position: Int): Fragment {
        return PostDetailFragment.newInstance(postIdList[position])
    }
}
