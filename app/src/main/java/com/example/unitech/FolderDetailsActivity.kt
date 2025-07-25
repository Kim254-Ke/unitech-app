package com.example.unitech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FolderDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_details)

        Log.d("FolderDetailsActivity: ", "FolderDetailsActivity")

        val unitDetails = intent.getStringExtra("unitDetails")
        val examQuestionsCard: CardView = findViewById(R.id.card_exam_questions)

        examQuestionsCard.setOnClickListener {
            if (unitDetails != null) {
                val intent = Intent(this, SetExamQuestionsActivity::class.java)
                intent.putExtra("UnitDetails", unitDetails)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Major Error Alert. Please Inform The Developer!!!", Toast.LENGTH_LONG).show()
            }
        }

        Log.d("StudentExamQuestionsDisplay", "++++++++++++++++++++++++unitDetails: $unitDetails")

        val studentAnswersCard: CardView = findViewById(R.id.card_Student_Answers)
        studentAnswersCard.setOnClickListener {
            if (unitDetails != null) {
                fetchStudentDetails(unitDetails.trim())
            } else {
                Toast.makeText(this, "Unit details is empty.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchStudentDetails(trimmedUnitDetails: String) {
        // Launch a coroutine in the lifecycleScope tied to the Activity
        lifecycleScope.launch(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val studentDetailsRef = db.collection("ExamMgt").document(trimmedUnitDetails).collection("StudentDetails").document("AllStudentDetailsList")

            try {
                val documentSnapshot = studentDetailsRef.get().await()
                if (documentSnapshot.exists()) {
                    val studentDetailsList = documentSnapshot.data.toString()
                    val newString = studentDetailsList.substringAfter("StudentDetailsList=").substringBefore("}")
                    val items = newString.removeSurrounding("[", "]").split(",").map { it.trim() }
                    val newStudentDetailsList = items.toMutableList().toString()

                    // Switch back to the main thread for UI updates
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@FolderDetailsActivity, "Student Details found: $newStudentDetailsList.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@FolderDetailsActivity, StudentDetailsListActivity::class.java)
                        intent.putExtra("newStudentDetailsList", newStudentDetailsList)
                        intent.putExtra("unitDetails", trimmedUnitDetails)
                        startActivity(intent)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@FolderDetailsActivity, "No student has carried out the exam.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("FolderDetailsActivity", "Error fetching student details", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FolderDetailsActivity, "Error fetching student details.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
