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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class StudentExamActivity : AppCompatActivity() {
    private val TAG = "StudentExamActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_exam_activity)

        Log.d(TAG, "StudentExamActivity" +
                "" +
                "" +
                "" +
                ".")

        val db = FirebaseFirestore.getInstance()
        val unitReference = db.collection("ExamMgt").document("PendingExamUnitsList")
        unitReference.get().addOnSuccessListener { document ->
            Log.d(TAG, "Successfully retrieved documents.")
            Toast.makeText(this, "Successfully retrieved documents.", Toast.LENGTH_LONG).show()
            if (document.exists()) {
                val folderDetails = document.data.toString()
                Toast.makeText(this, "Folder details found: $folderDetails.", Toast.LENGTH_LONG).show()
                val newString = folderDetails.substringAfter("PendingExamUnitsList=").substringBefore("}")
                val items = newString.removeSurrounding("[", "]").split(",").map { it.trim() }
                val folderDetailsList = items.toMutableList()

                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                val adapter = FolderListAdapter(this, folderDetailsList)
                recyclerView.layoutManager = GridLayoutManager(this, 2)
                recyclerView.adapter = adapter
            } else {
                Toast.makeText(this, "No Exam Has Been Set.", Toast.LENGTH_LONG).show()
            }

        }.addOnFailureListener {
            Log.e(TAG, "Error getting documents: ", it)
        }
    }

    inner class FolderListAdapter(private val context: Context, private val folderDetailsList: MutableList<String>) :
        RecyclerView.Adapter<FolderListAdapter.ViewHolder>() {

        private val TAG_ADAPTER = "FolderListAdapter"
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.textViewFolderItem)
            val cardStudentExamView: CardView = view.findViewById(R.id.CardStudentExamView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            Log.d(TAG_ADAPTER, "onCreateViewHolder: Creating view holder.")
            val view = LayoutInflater.from(parent.context).inflate(R.layout.student_item_folder, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val studentDetails = sharedPref.getString("userEmail", null)

            Log.d(TAG_ADAPTER, "onBindViewHolder: Position $position")

            val stringedUnitName = folderDetailsList[position]
            val newStringedUnitName = stringedUnitName.replace("%%%%%SPACE%%%%%"," ")
                .replace("%%%%%COLON%%%%%", ":")
                .replace("%%%%%FOSTROKE%%%%%", "/")
                .replace("%%%%%BACKSLASH%%%%%", "\\")
                .replace("{", "%%%%%OPCURLY%%%%%")
                .replace("%%%%%CLOCURLY%%%%%", "}")
                .replace("%%%%%STAR%%%%%", "*")
                .replace("%%%%%QUESTIONMARK%%%%%", "?")
                .replace("%%%%%EXCLAMATIONMARK%%%%%", "!")
                .replace("%%%%%COMMA%%%%%", ",")
                .replace("%%%%%nextLine%%%%%", "\n")

            holder.textView.text = newStringedUnitName
            val unitDetails = folderDetailsList[position]
            Log.d(TAG_ADAPTER, "UnitDetails: $unitDetails")
            Log.d(TAG_ADAPTER, "FolderDetailsList: $folderDetailsList")

            holder.cardStudentExamView.setOnClickListener {
                if (studentDetails != null){
                    saveStudentDetailsToFireBase(unitDetails, studentDetails, object : FirebaseCallback {
                        override fun onCallback() {
                            val intent = Intent(context, QuestionDetailActivity::class.java)
                            intent.putExtra("unitDetails", unitDetails)
                            intent.putExtra("studentDetails", studentDetails) // Pass the student details
                            context.startActivity(intent) // Make sure to use context.startActivity
                        }
                    })
                }
                else{
                    Toast.makeText(context, "Student details, email or registration number is null. Please inform the developer!!!!", Toast.LENGTH_LONG).show()
                }
            }
        }

        override fun getItemCount(): Int {
            return folderDetailsList.size
        }
    }

    interface FirebaseCallback {
        fun onCallback()
    }

    private fun saveStudentDetailsToFireBase(unitDetails: String, studentDetails: String, callback: FirebaseCallback) {
        Log.d("FolderListAdapter", "---------------------------")
        val db = FirebaseFirestore.getInstance()
        val studentDetailsRef = db.collection("ExamMgt")
            .document(unitDetails)
            .collection("StudentDetails")
            .document("AllStudentDetailsList")

        studentDetailsRef.get().addOnSuccessListener { document ->
            val newStudentDetailsList = mutableListOf<String>()

            if (document.exists()) {
                val studentDetailsList = document.get("StudentDetailsList") as List<*>?
                if (studentDetailsList != null) {
                    newStudentDetailsList.addAll(studentDetailsList.filterIsInstance<String>())
                }
            }

            // Check if studentDetails already exists
            if (newStudentDetailsList.contains(studentDetails)) {
                Log.d("MyApp", "Student details already exist, not saving again.")
                callback.onCallback()
            } else {
                // Only add and save if the student is NOT in the list
                newStudentDetailsList.add(studentDetails)

                val studentDetailsListMap = hashMapOf(
                    "StudentDetailsList" to newStudentDetailsList
                )

                studentDetailsRef.set(studentDetailsListMap)
                    .addOnSuccessListener {
                        Log.d("MyApp", "Student details saved successfully")
                        callback.onCallback()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Error saving student details", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.w("Error fetching student details", e)
        }
    }
}
