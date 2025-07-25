package com.example.unitech

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class teacherCheckStudentsAnswersActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_answers)

        Log.d("teacherCheckStudentsAnswersActivity", "teacherCheckStudentsAnswersActivity!!!!")
        val originalStudentDetails = intent.getStringExtra("studentDetails")?.trim()
        var studentDetails = ""
        if (originalStudentDetails != null) {
            studentDetails = originalStudentDetails
        } else {
            Toast.makeText(this, "Major error alert. Student details list is empty. Please inform the developer.", Toast.LENGTH_LONG).show()
        }

        val originalUnitDetails = intent.getStringExtra("unitDetails")?.trim()
        var unitDetails = ""
        if (originalUnitDetails != null) {
            unitDetails = originalUnitDetails
        } else {
            Toast.makeText(this, "Major error alert. Unit details is empty. Please inform the developer.", Toast.LENGTH_LONG).show()
        }

        val originalQuestionAnswerNumber = intent.getStringExtra("questionAnswerNumber")?.trim()
        var questionAnswerNumber = ""
        if (originalQuestionAnswerNumber != null) {
            questionAnswerNumber = originalQuestionAnswerNumber
        } else {
            Toast.makeText(this, "Major error alert. Question Answer Number is empty. Please inform the developer.", Toast.LENGTH_LONG).show()
        }

        val answersViewPager: ViewPager2 = findViewById(R.id.answersViewPager)
        val answersTabLayout: TabLayout = findViewById(R.id.answersTabLayout)


        fetchResultsAndStudentAnswer(unitDetails, studentDetails, questionAnswerNumber) { resultList, studentsAnswer ->

            lifecycleScope.launch(Dispatchers.Main) {
                if (!resultList.toString().contains("%%%Tester%%%") && !studentsAnswer.toString().contains("%%%%Tester%%%")){
                    Log.d("11111111111111", "11111111111")
                    Log.d("resultList111", "resultList1111111111: $resultList")
                    Log.d("studentsAnswer111", "studentsAnswer111111111: $studentsAnswer")
                    val newAnswersViewPagerAdapter = answersViewPagerAdapter(this@teacherCheckStudentsAnswersActivity, studentsAnswer, resultList)
                    answersViewPager.adapter = newAnswersViewPagerAdapter

                    val tabNames = listOf("Student's Answer", "Marked Answer")
                    TabLayoutMediator(answersTabLayout, answersViewPager) { tab, position ->
                        tab.text = tabNames[position]
                    }.attach()
                }
                else if (resultList.toString().contains("%%%Tester%%%") && resultList != null){
                    Toast.makeText(this@teacherCheckStudentsAnswersActivity, "Failed to fetch results", Toast.LENGTH_LONG).show()
                }
                else if (studentsAnswer != null) {
                    if (studentsAnswer.contains("%%%%%%Tester%%%%%")){
                        Toast.makeText(this@teacherCheckStudentsAnswersActivity, "Failed to fetch students answers", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun fetchResultsAndStudentAnswer(unitDetails: String, studentDetails: String, questionAnswerNumber: String, onComplete: (String?, String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        val resultsCollectionRef = db.collection("ExamMgt").document(unitDetails).collection("Results").document(studentDetails).collection("result").document("Question${questionAnswerNumber.trim()}")
        Log.d("questionAnswerNumber111111111", "questionAnswerNumber111111111: $questionAnswerNumber")
        val studentAnswersRef = db.collection("ExamMgt").document(unitDetails).collection("studentAnswers").document(studentDetails).collection("studentAnswer").document("Question${questionAnswerNumber}")


        var resultList: String? = null
        var studentsAnswer: String? = null
        Log.d("resultListtfvvvvv", "resultListttfvvvvv: $resultList")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                try{
                    val resultsCollectionSnapshot = resultsCollectionRef.get().await()
                    if (resultsCollectionSnapshot.exists()) {
                        resultList = resultsCollectionSnapshot.get("result") as? String
                        launch(Dispatchers.Main) {
                            Toast.makeText(this@teacherCheckStudentsAnswersActivity, "resultList: $resultList", Toast.LENGTH_LONG).show()
                            Log.d("resultList5555555", "resultList555555555: $resultList")
                        }
                    }
                    else{
                        launch(Dispatchers.Main) {
                            Toast.makeText(this@teacherCheckStudentsAnswersActivity, "No exam results have been saved!!!!!!", Toast.LENGTH_LONG).show()
                            Log.d("resultList", "No exam results have been saved!!!!!!")
                        }
                    }

                }catch (e: Exception) {
                    resultList = "Failed to fetch results: ${e.message}%%%%%%Tester%%%%%"
                    Toast.makeText(this@teacherCheckStudentsAnswersActivity, "Failed to fetch results: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("FirestoreError", "Failed to fetch results: ${e.message}", e)
                }
                try{
                    val studentAnswersSnapshot = studentAnswersRef.get().await()
                    if (studentAnswersSnapshot.exists()) {
                        studentsAnswer = (studentAnswersSnapshot.get("answer") as? String)
                        launch(Dispatchers.Main) {
                            Toast.makeText(this@teacherCheckStudentsAnswersActivity, "studentsAnswer: $studentsAnswer", Toast.LENGTH_LONG).show()
                            Log.d("studentsAnswer", "studentsAnswer: $studentsAnswer")
                        }
                    }
                    else{
                        launch(Dispatchers.Main) {
                            Toast.makeText(this@teacherCheckStudentsAnswersActivity, "No studentsAnswer have been saved!!!!!!", Toast.LENGTH_LONG).show()
                            Log.d("teacherCheckStudentsAnswersActivity", "No studentsAnswer have been saved!!!!!!")
                        }
                    }
                }catch (e: Exception) {
                    studentsAnswer = "Failed to fetch students answers: ${e.message}%%%%%%Tester%%%%%"
                    Toast.makeText(this@teacherCheckStudentsAnswersActivity, "Failed to fetch students answers: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("FirestoreError", "Failed to fetch students answers: ${e.message}", e)
                }
                Log.d("resultList", "resultList!!!!: $resultList")
                Log.d("studentsAnswer", "studentsAnswer!!!!: $studentsAnswer")
                onComplete(resultList, studentsAnswer)

            }catch (e: Exception) {
                resultList = "Failed to fetch results: ${e.message}%%%%%%Tester%%%%%"
                studentsAnswer = "Failed to fetch students answers: ${e.message}%%%%%%Tester%%%%%"

                Toast.makeText(this@teacherCheckStudentsAnswersActivity, "Failed to fetch results and students answers: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("FirestoreError", "Failed to fetch results and students answers: ${e.message}", e)
                onComplete(resultList, studentsAnswer)
            }
        }
    }

}