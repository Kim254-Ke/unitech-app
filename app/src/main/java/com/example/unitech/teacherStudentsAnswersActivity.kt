package com.example.unitech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class teacherStudentsAnswersActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var teacherStudentsAnswersAdapter: TeacherStudentsAnswersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_answers_list)

        val db = FirebaseFirestore.getInstance()
        val originalUnitDetails = intent.getStringExtra("unitDetails")
        var unitDetails = ""
        if (originalUnitDetails != null) {
            unitDetails = originalUnitDetails
        } else {
            Toast.makeText(this, "Major error alert. Unit details is empty. Please inform the developer.", Toast.LENGTH_LONG).show()
        }

        val originalStudentDetails = intent.getStringExtra("studentDetails")
        var studentDetails = ""
        if (originalStudentDetails != null) {
            studentDetails = originalStudentDetails
        } else {
            Toast.makeText(this, "Major error alert. Student details is empty. Please inform the developer.", Toast.LENGTH_LONG).show()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val studentRef =  db.collection("ExamMgt").document(unitDetails).collection("studentAnswers").document(studentDetails.trim()).collection("studentAnswer")
            try {
                val document = studentRef.get().await()
                if (document.isEmpty){

                    Log.d("TAG", "studentDetails: $studentDetails")
                    Log.d("TAG", "The student has not answered any question.")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@teacherStudentsAnswersActivity, "The student has not answered any question", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    val numberOfQuestions = document.size()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@teacherStudentsAnswersActivity, "Successfully retrieved the required documents from firestore.", Toast.LENGTH_LONG).show()
                    }
                    val questionNumbersList = mutableListOf<Int>()
                    var i = 1
                    while (i <= numberOfQuestions){
                        questionNumbersList.add(i)
                        i += 1
                    }
                    Log.d("questionNumbersList", "questionNumbersList: $questionNumbersList")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@teacherStudentsAnswersActivity, "questionNumbersList: ${questionNumbersList}", Toast.LENGTH_LONG).show()
                    }

                    withContext(Dispatchers.Main) {  // Update UI on the main thread
                        Toast.makeText(this@teacherStudentsAnswersActivity, "Successfully retrieved the required documents from Firestore.", Toast.LENGTH_LONG).show()
                        teacherStudentsAnswersAdapter = TeacherStudentsAnswersAdapter(this@teacherStudentsAnswersActivity, questionNumbersList, unitDetails, studentDetails)
                        recyclerView = findViewById(R.id.studentQuestionsAnswersRecyclerView)
                        recyclerView.layoutManager = GridLayoutManager(this@teacherStudentsAnswersActivity, 2)
                        recyclerView.adapter = teacherStudentsAnswersAdapter
                    }
                }
            }catch (e: Exception) {
                Log.e("TAG", "Error fetching student answers: ", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@teacherStudentsAnswersActivity, "Failed to retrieve documents.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    inner class TeacherStudentsAnswersAdapter(private val context: Context, private val questionNumbersList: MutableList<Int>, private val unitDetails: String, private val studentDetails: String) : RecyclerView.Adapter<TeacherStudentsAnswersAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val answerNumberCard: CardView = itemView.findViewById(R.id.answerNumberCard)
            val answerNumber: TextView = itemView.findViewById(R.id.answerNumber)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.student_answers_items, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.answerNumber.text = questionNumbersList[position].toString()
            val questionAnswerNumber = questionNumbersList[position].toString()

            holder.answerNumberCard.setOnClickListener{
                val intent = Intent(context, teacherCheckStudentsAnswersActivity::class.java)
                intent.putExtra("studentDetails", studentDetails)
                intent.putExtra("unitDetails", unitDetails)
                intent.putExtra("questionAnswerNumber", questionAnswerNumber)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return questionNumbersList.size
        }
    }
}