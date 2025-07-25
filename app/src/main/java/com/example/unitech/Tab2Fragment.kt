package com.example.unitech

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class Tab2Fragment(private val unitDetails: String, private val questionNumber: String) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab2, container, false)

        val db = FirebaseFirestore.getInstance()
        val answerEditText: EditText = view.findViewById(R.id.editTextAnswer)
        val submitButton: Button = view.findViewById(R.id.buttonSubmitAnswer)

        val saveTeacherAnswerReference = db.collection("ExamMgt")
            .document(unitDetails)
            .collection("Answer")
            .document(questionNumber)

        // Fetch existing answer if available
        saveTeacherAnswerReference.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val answer = document.getString("answer")
                    answerEditText.setText(answer)
                }
            }
            .addOnFailureListener {
                Log.e("FirestoreError", "Failed to fetch answer: ${it.message}", it)
                Toast.makeText(requireContext(), "Failed to fetch answer", Toast.LENGTH_SHORT).show()
            }

        submitButton.setOnClickListener {
            val answer = answerEditText.text.toString()

            if (answer.isNotEmpty()) {
                val teacherAnswerMap = hashMapOf("answer" to answer)
                saveTeacherAnswerReference.set(teacherAnswerMap)
                    .addOnSuccessListener {
                        Log.d("FirestoreSuccess", "Successfully saved Answer")
                        Toast.makeText(requireContext(), "Answer Saved successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Log.e("FirestoreError", "Failed to save answer, error: ${it.message}", it)
                        Toast.makeText(requireContext(), "Failed To Save Answer, error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Please enter an answer", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
