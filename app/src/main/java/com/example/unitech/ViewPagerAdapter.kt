package com.example.unitech

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity, unitDetails: String, questionNumber: String) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentList = listOf(
        Tab1Fragment(unitDetails, questionNumber),
        Tab2Fragment(unitDetails, questionNumber),
        Tab3Fragment(unitDetails, questionNumber)
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}
