package com.example.umc_closit.ui.profile.highlight

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HighlightDetailPagerAdapter(
    activity: AppCompatActivity,
    private var postIdList: List<Int>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = postIdList.size

    override fun createFragment(position: Int): Fragment {
        return PostDetailFragment.newInstance(postIdList[position], showButton = false)
    }

    fun updateList(newList: List<Int>) {
        postIdList = newList
        notifyDataSetChanged()
    }
}
