package com.example.unitech

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class StudentExamQuestionsDisplay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_exam)

        val unitNameDetails = intent.getStringExtra("unitNameDetails") ?: "%%%%%%%%%%null%%%%%%%%%%%"
        val studentDetails = intent.getStringExtra("studentDetails") ?: "%%%%%%%%%%null%%%%%%%%%%%"
        val questionNumber = intent.getStringExtra("questionNumber") ?: "%%%%%%%%%%null%%%%%%%%%%%"

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        val adapter = studentQuestionAnswerPagerAdapter(this, unitNameDetails, studentDetails, questionNumber)
        viewPager.adapter = adapter

        val tabNames = listOf("Question", "Answer")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }

    private inner class studentQuestionAnswerPagerAdapter(fragmentActivity: FragmentActivity, unitNameDetails: String, studentDetails: String, questionNumber: String) : FragmentStateAdapter(fragmentActivity) {

        private val fragmentList = listOf(
            QuestionFragment(questionNumber, unitNameDetails),
            AnswerFragment(unitNameDetails, studentDetails, questionNumber)
        )

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }
    }
}
