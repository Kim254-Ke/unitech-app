package com.example.unitech

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SetExamQuestionsActivity : AppCompatActivity() {
    private val questionNumbers = mutableListOf<String>()
    private lateinit var adapter: ExamQuestionsAdapter
    private val TAG = "SetExamQuestionsActivity"
    private var itemId = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_exam_questions)

        var UnitDetails = intent.getStringExtra("UnitDetails")
        if (UnitDetails != null){
            UnitDetails = UnitDetails.trim()

            questionNumbers.add("Question${itemId++}")

            val rvExamQuestions: RecyclerView = findViewById(R.id.rvExamQuestions)
            adapter = ExamQuestionsAdapter(this, questionNumbers, UnitDetails)
            rvExamQuestions.layoutManager = GridLayoutManager(this, 2)
            rvExamQuestions.adapter = adapter

            val btnAddQuestion: Button = findViewById(R.id.btnAddQuestion)

            btnAddQuestion.setOnClickListener {
                val newQuestionNumber = "Question${itemId++}"
                questionNumbers.add(newQuestionNumber)
                adapter.updateNumbers(questionNumbers)
            }

            Toast.makeText(this, "UnitDetails: $UnitDetails", Toast.LENGTH_LONG).show()
            Log.d(TAG, "UUUUUUUnitDetails: " + UnitDetails)
        }
        else{
            Toast.makeText(this, "UnitDetails is empty. Please check with the developer.", Toast.LENGTH_LONG).show()
        }

    }
}
