package com.example.unitech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StudentDetailsListActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var studentDetailsListAdapter: StudentDetailsListAdapter
    private lateinit var unitDetails: String
    private val client = OkHttpClient()
    private val apiKEY = BuildConfig.OPENAI_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_details_list)

        val db = FirebaseFirestore.getInstance()

        val StudentDetailsList = intent.getStringExtra("newStudentDetailsList")?.trim()
        val originalUnitDetails = intent.getStringExtra("unitDetails")?.trim()

        if (originalUnitDetails != null) {
            unitDetails = originalUnitDetails.trim()
        } else {
            Toast.makeText(this, "Major error alert. Unit details is empty. Please inform the developer.", Toast.LENGTH_LONG).show()
        }

        val items = StudentDetailsList?.removeSurrounding("[", "]")?.split(",")
        val newStudentDetailsList = items?.toMutableList()
        if (newStudentDetailsList != null) {
            studentDetailsListAdapter = StudentDetailsListAdapter(this, newStudentDetailsList, unitDetails)
            recyclerView = findViewById(R.id.studentDetailsListRecyclerView)
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            recyclerView.adapter = studentDetailsListAdapter
        } else {
            Toast.makeText(this, "Major error alert. Student details list is empty. Please inform the developer. It could be that no student has done the exam yet. Please check", Toast.LENGTH_LONG).show()
        }

        val markExamsButton: Button = findViewById(R.id.markExamsButton)
        markExamsButton.setOnClickListener {
            Log.d("clicked", "cccccccccccccclicked")
            Log.d("newStudentDetailsList", "newStudentDetailsList: $newStudentDetailsList")
            if (newStudentDetailsList != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    for (student in newStudentDetailsList) {
                        Log.d("unitDetails", "unitDetails:$unitDetails")
                        Log.d("student", "student1111111111111111111:$student")

                        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////To determine the number of documents in a collection, use the code below; not sure if it'll work well though

                        val documentReference = db.collection("ExamMgt").document(unitDetails).collection("Question")
                        Log.d("kfv", "efdccdcc")
                        try {
                            Log.d("555555555555555555", "kkkkkkkkkk")
                            val documentReferenceSnapshot = documentReference.get().await()
                            if (documentReferenceSnapshot.isEmpty) {
                                Log.d("TAG", "No questions have been set.")
                                Log.d("student", "student1111111111111111111:$student")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@StudentDetailsListActivity, "No questions have been set", Toast.LENGTH_LONG).show()
                                }
                            }
                            else{
                                val numberOfQuestions = documentReferenceSnapshot.size()  // This will give you the number of documents
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@StudentDetailsListActivity, "Successfully retrieved the required documents from firestore.", Toast.LENGTH_LONG).show()
                                }

                                val questionNumbersList = mutableListOf<Int>()
                                var i = 1
                                while (i <= numberOfQuestions){
                                    questionNumbersList.add(i)
                                    i += 1
                                }
                                Log.d("questionNumbersList", "questionNumbersList: $questionNumbersList")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@StudentDetailsListActivity, "questionNumbersList: ${questionNumbersList}", Toast.LENGTH_LONG).show()
                                }

                                for (v in questionNumbersList){
                                    val questionNumber = v.toString()
                                    val teacherQuestionReference = db.collection("ExamMgt").document(unitDetails.trim()).collection("Question").document("Question${questionNumber.trim()}")
                                    val teacherAnswerReference = db.collection("ExamMgt").document(unitDetails.trim()).collection("Answer").document("Question${questionNumber.trim()}")
                                    val examMarkingRulesReference = db.collection("ExamMgt").document(unitDetails.trim()).collection("examMarkingRules").document("Question${questionNumber.trim()}")
                                    val marksReference = db.collection("ExamMgt").document(unitDetails.trim()).collection("Marks").document("Question${questionNumber.trim()}")

                                    val studentAnswersRef = db.collection("ExamMgt").document(unitDetails.trim()).collection("studentAnswers").document(student.trim()).collection("studentAnswer").document("Question${questionNumber.trim()}")

                                    try{
                                        val teacherQuestionSnapshot = teacherQuestionReference.get().await()
                                        if (teacherQuestionSnapshot.exists()){
                                            val preTeacherQuestion = teacherQuestionSnapshot.getString("Question")
                                            Log.d("preTeacherQuestion", "preTeacherQuestion: $preTeacherQuestion")
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(this@StudentDetailsListActivity, "preTeacherQuestion: ${preTeacherQuestion}", Toast.LENGTH_LONG).show()
                                            }

                                            try{
                                                val teacherAnswerSnapshot = teacherAnswerReference.get().await()
                                                if (teacherAnswerSnapshot.exists()){
                                                    val preTeacherAnswer = teacherAnswerSnapshot.getString("answer")
                                                    Log.d("preTeacherAnswer", "preTeacherAnswer: $preTeacherAnswer")
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(this@StudentDetailsListActivity, "preTeacherAnswer: ${preTeacherAnswer}", Toast.LENGTH_LONG).show()
                                                    }

                                                    try{
                                                        val examMarkingRulesSnapshot = examMarkingRulesReference.get().await()
                                                        if (examMarkingRulesSnapshot.exists()){
                                                            val preExamMarkingRules = examMarkingRulesSnapshot.getString("stringedExamMarkingRulesList")
                                                            Log.d("preExamMarkingRules", "preExamMarkingRules: $preExamMarkingRules")
                                                            withContext(Dispatchers.Main) {
                                                                Toast.makeText(this@StudentDetailsListActivity, "preExamMarkingRules: ${preExamMarkingRules}", Toast.LENGTH_LONG).show()
                                                            }

                                                            try{
                                                                val marksReferenceSnapshot = marksReference.get().await()
                                                                if (marksReferenceSnapshot.exists()){
                                                                    val marks = marksReferenceSnapshot.getString("Marks")

                                                                    try{
                                                                        val studentAnswersSnapshot = studentAnswersRef.get().await()
                                                                        Log.d("student", "student222222: $student")
                                                                        Log.d("QuestionNumber: ","22222222Question${questionNumber}")
                                                                        if (studentAnswersSnapshot.exists()){
                                                                            val preStudentAnswer = studentAnswersSnapshot.getString("answer")
                                                                            Log.d("preStudentAnswer", "preStudentAnswer: $preStudentAnswer")
                                                                            withContext(Dispatchers.Main) {
                                                                                Toast.makeText(this@StudentDetailsListActivity, "preStudentAnswer: ${preStudentAnswer}", Toast.LENGTH_LONG).show()
                                                                            }

                                                                            val prompt = """I am a teacher evaluating a student's answer to a given question, and I need your help. I will provide the question, the expected correct answer, the student's answer and the maximum marks to be awarded.Your task is to compare the student's answer with the correct answer and identify sections that are correct or incorrect. Provide feedback in the format [[status, start_letter_index, end_letter_index], ...[marks]], where status is either "correct" for sections that match the correct answer or "incorrect" for sections that do not, the start_letter_index and end_letter_index are the letter's indices indicating the range of letters in the student's answer that correspond to the status and marks are the maximum marks to be awarded. For example, if the correct answer is "Solid, liquid, and gas" and the student's answer is "Solid, liquid, and air," the output would be [[correct, 0, 12], [incorrect, 19, 21], [2]]. The marks to be awarded will be stored in the last list of the nested list, e.g. [2], as is in the list in the example provided. Please ignore case and minor punctuation differences when evaluating. Act like you are a human teacher, you may also browse the internet to learn how human teachers usually mark and apply the same principles. Try to be as human as possible. Question: ${preTeacherQuestion}, Correct Answer: ${preTeacherAnswer}, Student Answer: $preStudentAnswer, Marks: ${marks}. Gauge how many marks should be awarded like a reasonable teacher. If their is a correct part and a wrong part the marks can still be zero.Think critically when marking and awarding marks.""".trimIndent()

                                                                            Log.d("prompt", "prompt: $prompt")
                                                                            Log.d("student", "student1111111111111111111:$student")
                                                                            withContext(Dispatchers.Main) {
                                                                                Toast.makeText(this@StudentDetailsListActivity, "prompt: ${prompt}", Toast.LENGTH_LONG).show()
                                                                            }

                                                                            // Suspend execution and wait for the result
                                                                            val result = withContext(Dispatchers.IO) {
                                                                                evaluateAnswerAsync(prompt)
                                                                            }

                                                                            val resultsCollectionRef = db.collection("ExamMgt").document(unitDetails.trim()).collection("Results").document(student.trim()).collection("result").document("Question${questionNumber.trim()}")

                                                                            val resultMap = hashMapOf("result" to result)

                                                                            withContext(Dispatchers.IO) {
                                                                                resultsCollectionRef.set(resultMap).await()
                                                                            }

                                                                            withContext(Dispatchers.Main) {
                                                                                Toast.makeText(this@StudentDetailsListActivity, "Marking Successful", Toast.LENGTH_LONG).show()
                                                                                Log.d("Marking Successful", "Marking Successful")
                                                                            }

                                                                        }else{
                                                                            Log.d("studentAnswer", "The student has not provided answers to the questions given.")
                                                                            Log.d("student", "student1111111111111111111:$student")
                                                                            withContext(Dispatchers.Main) {
                                                                                Toast.makeText(this@StudentDetailsListActivity, "The student has not provided answers to the questions given.", Toast.LENGTH_LONG).show()
                                                                            }
                                                                        }
                                                                    }catch (e: Exception) {
                                                                        Log.e("FirestoreError", "Failed to fetch student Answers, error: ${e.message}", e)
                                                                        withContext(Dispatchers.Main) {
                                                                            Toast.makeText(this@StudentDetailsListActivity, "Failed to fetch student Answers, error: ${e.message}", Toast.LENGTH_LONG).show()
                                                                        }
                                                                    }

                                                                }
                                                                else {
                                                                    withContext(Dispatchers.Main) {
                                                                        Toast.makeText(this@StudentDetailsListActivity, "The marks for the question were not provided by the teacher. Inform both the developer and teacher.", Toast.LENGTH_LONG).show()
                                                                    }
                                                                }
                                                            }catch (e: Exception) {
                                                                Log.e("FirestoreError", "Failed to fetch question's marks, error: ${e.message}", e)
                                                                withContext(Dispatchers.Main) {
                                                                    Toast.makeText(this@StudentDetailsListActivity, "Failed to fetch question's marks, error: ${e.message}", Toast.LENGTH_LONG).show()
                                                                }
                                                            }
                                                        } else{
                                                            Log.d("Exam Marking Rules", "The rules to be used in marking have not been provided by the teacher.")
                                                            withContext(Dispatchers.Main) {
                                                                Toast.makeText(this@StudentDetailsListActivity, "The rules to be used in marking have not been provided by the teacher.", Toast.LENGTH_LONG).show()
                                                            }
                                                        }
                                                    }catch (e: Exception) {
                                                        Log.e("FirestoreError", "Failed to fetch exam Marking Rules, error: ${e.message}", e)
                                                        withContext(Dispatchers.Main) {
                                                            Toast.makeText(this@StudentDetailsListActivity, "Failed to fetch exam Marking Rules, error: ${e.message}", Toast.LENGTH_LONG).show()
                                                        }
                                                    }
                                                } else{
                                                    Log.d("TeacherAnswer", "The answer to the question has not been provided by the teacher.")
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(this@StudentDetailsListActivity, "The answer to the question has not been provided by the teacher", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }catch (e: Exception) {
                                                Log.e("FirestoreError", "Failed to fetch teachers Answer, error: ${e.message}", e)
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(this@StudentDetailsListActivity, "Failed to fetch teachers Answer, error: ${e.message}", Toast.LENGTH_LONG).show()
                                                }
                                            }

                                        } else{
                                            Log.d("TeacherQuestion", "No question has been set")
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(this@StudentDetailsListActivity, "No question has been set", Toast.LENGTH_LONG).show()
                                            }

                                        }
                                    }catch (e: Exception) {
                                        Log.e("FirestoreError", "Failed to fetch teachers Question, error: ${e.message}", e)
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(this@StudentDetailsListActivity, "Failed to fetch teachers Question, error: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }

                        }catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@StudentDetailsListActivity, "Failure retrieving the required documents from firestore, error: ${e.message}", Toast.LENGTH_LONG).show()
                            }

                        }
                    }
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                }
            }
        }
    }
    private suspend fun evaluateAnswerAsync(prompt: String): String = suspendCancellableCoroutine { continuation ->
        val json = JSONObject().apply {
            put("model", "gpt-4o")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("max_tokens", 500)
            put("temperature", 0)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = RequestBody.create(mediaType, json.toString())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        continuation.resumeWithException(IOException("Unexpected response code: ${it.code}"))
                        return
                    }

                    val responseData = it.body?.string()
                    try {
                        val jsonResponse = JSONObject(responseData)
                        val completion = jsonResponse.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                            .trim()
                        continuation.resume(completion)
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            }
        })
    }

    inner class StudentDetailsListAdapter(private val context: Context, private val newStudentDetailsList: MutableList<String>, private val unitDetails: String) :
        RecyclerView.Adapter<StudentDetailsListAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val studentDetailsCard: CardView = itemView.findViewById(R.id.studentDetailsCard)
            var studentName: TextView = itemView.findViewById(R.id.studentName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.student_details_items, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.studentName.text = newStudentDetailsList[position]
            val studentDetails = newStudentDetailsList[position]
            holder.studentDetailsCard.setOnClickListener {
                val intent = Intent(context, teacherStudentsAnswersActivity::class.java)
                intent.putExtra("studentDetails", studentDetails)
                intent.putExtra("unitDetails", unitDetails)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return newStudentDetailsList.size
        }
    }

}