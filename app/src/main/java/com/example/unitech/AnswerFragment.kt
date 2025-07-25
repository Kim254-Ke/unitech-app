package com.example.unitech

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AnswerFragment(private val unitNameDetails: String, private val studentDetails: String, private val questionNumber: String) : Fragment() {
    private lateinit var answerEditText: EditText
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_answer, container, false)

        answerEditText = view.findViewById(R.id.answerEditText)
        val submitButton: Button = view.findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            submitAnswer()
        }

        return view
    }

    private fun submitAnswer() {
        val db = FirebaseFirestore.getInstance()

        val answer = answerEditText.text.toString()
        if (answer.isEmpty()) {
            Toast.makeText(requireContext(), "Answer cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val answerData = hashMapOf(
            "answer" to answer,
            "timestamp" to System.currentTimeMillis()
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                db.collection("ExamMgt").document(unitNameDetails)
                    .collection("studentAnswers").document(studentDetails)
                    .collection("studentAnswer").document("Question${questionNumber}")
                    .set(answerData).await()

                Log.d("successful", "SUCCESSFUL")
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Answer submitted successfully.", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception) {
                Log.e("Failed", "Failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to submit answer: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}