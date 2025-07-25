package com.example.unitech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class QuestionDetailActivity : AppCompatActivity() {
    private lateinit var unitNameDetails: String
    private lateinit var studentDetails: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        unitNameDetails = intent.getStringExtra("unitDetails") ?: "%%%%%%%%%%null%%%%%%%%%%%"
        studentDetails = intent.getStringExtra("studentDetails") ?: "%%%%%%%%%%null%%%%%%%%%%%"

        val db = FirebaseFirestore.getInstance()
        //To determine the number of documents in a collection, use the code below; not sure if it'll work well though
        val QuestionReference = db.collection("ExamMgt").document(unitNameDetails).collection("Question")
        QuestionReference.get().addOnSuccessListener { documents ->
            val numberOfQuestions = documents.size()  // This will give you the number of documents
            Toast.makeText(this, "Successfully retrieved the required documents from firestore.", Toast.LENGTH_LONG).show()

            val questionNumbersList = mutableListOf<Int>()
            var i = 1
            while (i <= numberOfQuestions){
                questionNumbersList.add(i)
                i += 1
            }
            val questionNumberRecyclerView: RecyclerView = findViewById(R.id.questionNumberRecyclerView)
            val adapter = QuestionDetailsAdapter(this, questionNumbersList)
            questionNumberRecyclerView.layoutManager = GridLayoutManager(this, 2)
            questionNumberRecyclerView.adapter = adapter

        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failure retrieving the required documents from firestore.", Toast.LENGTH_LONG).show()
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    inner class QuestionDetailsAdapter(private val context: Context, private val questionNumbersList: MutableList<Int>) : RecyclerView.Adapter<QuestionDetailsAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val QuestionNumberTextView: TextView = itemView.findViewById(R.id.QuestionNumberTextView)
            val CardQuestionNumber: CardView = itemView.findViewById(R.id.CardQuestionNumber)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.questions_card_display_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.QuestionNumberTextView.text = questionNumbersList[position].toString()
            val questionNumber = questionNumbersList[position]
            holder.CardQuestionNumber.setOnClickListener{
                val intent = Intent(context, StudentExamQuestionsDisplay::class.java)
                intent.putExtra("unitNameDetails", unitNameDetails)
                intent.putExtra("studentDetails", studentDetails)
                intent.putExtra("questionNumber", questionNumber.toString())
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return questionNumbersList.size
        }
    }
}
