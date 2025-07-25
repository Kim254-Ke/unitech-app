package com.example.unitech

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class StudentAnswerFragment(private val studentAnswer: String?) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_student_answer, container, false)

        val studentAnswerContentSection: TextView = view.findViewById(R.id.studentAnswerContent)
        if (studentAnswer != null){
            studentAnswerContentSection.text = studentAnswer
        }
        else{
            studentAnswerContentSection.text = "Students answer not available"
            Toast.makeText(context, "Students answer not available", Toast.LENGTH_LONG).show()
        }

        return view
    }
}
