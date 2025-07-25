package com.example.unitech

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class QuestionsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_questions_layout)

        val unitDetails = intent.getStringExtra("UnitDetails").toString()
        val questionNumber = intent.getStringExtra("questionNumber").toString()

        Log.d("22222222222222333333333", "4444444444unitDetails: $unitDetails")
        Log.d("22222222222222333333333questionNumber", "4444444questionNumber: $questionNumber")

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        val viewPagerAdapter = ViewPagerAdapter(this, unitDetails, questionNumber)
        viewPager.adapter = viewPagerAdapter

        val tabNames = listOf("Questions", "Answers", "Rules")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }
}