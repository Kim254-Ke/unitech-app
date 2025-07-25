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
import com.google.firebase.firestore.FirebaseFirestore

class Tab1Fragment(private val unitDetails: String, private val questionNumber: String) : Fragment() {
    private lateinit var questionEditText: EditText
    private lateinit var marksEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab1, container, false)

        Log.d("unitDetails", "unitDetails: $unitDetails")
        Log.d("questionNumber", "questionNumber: $questionNumber")

        // Find UI elements
        questionEditText = view.findViewById(R.id.editTextQuestionContent)
        marksEditText = view.findViewById(R.id.editTextMarksAwarded) // Get marks input field
        submitButton = view.findViewById(R.id.buttonSubmitQuestion)

        submitButton.isEnabled = false

        db = FirebaseFirestore.getInstance()

        // Load saved question & marks
        loadSavedQuestionAndMarks()

        submitButton.setOnClickListener {
            saveQuestionAndMarks()
        }

        return view
    }

    private fun loadSavedQuestionAndMarks() {
        val savedQuestionReference = db.collection("ExamMgt")
            .document(unitDetails)
            .collection("Question")
            .document(questionNumber)

        val savedMarksReference = db.collection("ExamMgt")
            .document(unitDetails)
            .collection("Marks")
            .document(questionNumber)

        // Load saved question
        savedQuestionReference.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val savedQuestion = document.getString("Question") ?: ""
                questionEditText.setText(savedQuestion)
            }
            submitButton.isEnabled = true
        }.addOnFailureListener { exception ->
            Log.e("FirestoreError", "Error loading question: ${exception.message}", exception)
            Toast.makeText(requireContext(), "Error loading question", Toast.LENGTH_SHORT).show()
        }

        // Load saved marks
        savedMarksReference.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val savedMarks = document.getString("Marks") ?: ""
                marksEditText.setText(savedMarks)
            }
        }.addOnFailureListener { exception ->
            Log.e("FirestoreError", "Error loading marks: ${exception.message}", exception)
            Toast.makeText(requireContext(), "Error loading marks", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveQuestionAndMarks() {
        val question = questionEditText.text.toString().trim()
        val marks = marksEditText.text.toString().trim()

        if (question.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a question", Toast.LENGTH_SHORT).show()
            return
        }

        if (marks.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter marks awarded", Toast.LENGTH_SHORT).show()
            return
        }

        val saveQuestionReference = db.collection("ExamMgt")
            .document(unitDetails)
            .collection("Question")
            .document(questionNumber)

        val saveMarksReference = db.collection("ExamMgt")
            .document(unitDetails)
            .collection("Marks")
            .document(questionNumber)

        val questionData = hashMapOf("Question" to question)
        val marksData = hashMapOf("Marks" to marks)

        // Save question and marks to Firestore
        saveQuestionReference.set(questionData).addOnSuccessListener {
            Log.d("FirestoreSuccess", "Successfully saved question")
        }.addOnFailureListener { exception ->
            Log.e("FirestoreError", "Failed to save question: ${exception.message}", exception)
            Toast.makeText(requireContext(), "Failed to save question", Toast.LENGTH_SHORT).show()
        }

        saveMarksReference.set(marksData).addOnSuccessListener {
            Log.d("FirestoreSuccess", "Successfully saved marks")
            Toast.makeText(requireContext(), "Question & Marks saved successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Log.e("FirestoreError", "Failed to save marks: ${exception.message}", exception)
            Toast.makeText(requireContext(), "Failed to save marks", Toast.LENGTH_SHORT).show()
        }
    }
}
