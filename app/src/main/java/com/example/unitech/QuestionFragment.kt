package com.example.unitech

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class QuestionFragment(private val questionNumber: String, private val unitNameDetails: String) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the fragment's layout
        val view = inflater.inflate(R.layout.fragment_question, container, false)

        Log.d("questionNumber11111111", "questionNumber11111111: $questionNumber")
        Log.d("unitNameDetails11111111", "unitNameDetails11111111: $unitNameDetails")
        // Retrieve and display the question from Firestore
        fetchQuestionFromDatabase(view)

        return view
    }

    private fun fetchQuestionFromDatabase(view: View) {
        val db = FirebaseFirestore.getInstance()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Firestore reference to the question document
                val questionReference = db.collection("ExamMgt")
                    .document(unitNameDetails.trim())
                    .collection("Question")
                    .document("Question$questionNumber")

                // Fetch the document snapshot asynchronously
                val documentSnapshot = questionReference.get().await()

                // Get the "Question" field from the document
                val question = documentSnapshot.getString("Question")
                Log.d("question11111111", "question11111111: $question")

                // Update the TextView on the main thread
                withContext(Dispatchers.Main) {
                    val questionTextView: TextView = view.findViewById(R.id.questionTextView)
                    Log.d("question222222222", "question22222222: $question")
                    if (question != null) {
                        questionTextView.text = question
                    } else {
                        questionTextView.text = "Question not found."
                    }
                }
            } catch (e: Exception) {
                Log.e("QuestionFragment", "Failed to read question from server: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to read question from server: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
