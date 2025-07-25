package com.example.unitech

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class answersViewPagerAdapter(fragmentActivity: FragmentActivity, studentAnswer: String?, resultList: String?) : FragmentStateAdapter(fragmentActivity) {
    private val fragmentList = listOf(
        StudentAnswerFragment(studentAnswer),
        MarkedAnswerFragment(studentAnswer, resultList)
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}