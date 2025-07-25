package com.example.unitech

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ExamManagementActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var folderAdapter: FolderAdapter
    private val newestPendingExamUnitsList = mutableListOf<String>()
    private lateinit var progressBar: ProgressBar

    val TAG = "ExamManagementActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_management)

        Log.d("ExamManagementActivity: ", "ExamManagementActivity")
        progressBar = findViewById(R.id.progressBar)

        readPendingExamUnitsListFromFirestore()
    }

    private fun showCreateFolderDialog(newestPendingExamUnitsList: MutableList<String>) {
        val editTextUnitName = EditText(this)
        editTextUnitName.hint = "Enter Unit Name"

        val editTextExamDate = EditText(this)
        editTextExamDate.hint = "Enter Exam Date (DD/MM/YYYY)"

        val editTextStartTime = EditText(this)
        editTextStartTime.hint = "Enter Start Time (HH:MM)"

        val editTextTimeLimit = EditText(this)
        editTextTimeLimit.hint = "Enter Time Limit (HH:MM)"

        val editTextGroup = EditText(this)
        editTextGroup.hint = "Enter Group (e.g., Year 4 Sem 1)"

        val Lecturer = EditText(this)
        Lecturer.hint = "Dr.Auni"

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(editTextUnitName)
        layout.addView(editTextExamDate)

        layout.addView(editTextStartTime)
        layout.addView(editTextTimeLimit)

        layout.addView(editTextGroup)
        layout.addView(Lecturer)


        AlertDialog.Builder(this)
            .setTitle("Create Folder")
            .setView(layout)
            .setPositiveButton("Create") { dialog, which ->
                val unitName = editTextUnitName.text.toString()
                val examDate = editTextExamDate.text.toString()

                val StartTime = editTextStartTime.text.toString()
                val TimeLimit = editTextTimeLimit.text.toString()

                if (!isValidDate(examDate) || !isValidTime(StartTime)) {
                    Toast.makeText(this, "Invalid date or time format. Please correct your input.", Toast.LENGTH_LONG).show()
                }

                else{
                    val group = editTextGroup.text.toString()
                    val LecConvert = Lecturer.text.toString()

                    var tempFolderDetails = "Unit$unitName %%%%%nextLine%%%%%Date$examDate %%%%%nextLine%%%%%Group$group %%%%%nextLine%%%%%Lecturer$LecConvert"
                    tempFolderDetails = tempFolderDetails.replace(" ", "%%%%%SPACE%%%%%")
                        .replace(":", "%%%%%COLON%%%%%")
                        .replace("/", "%%%%%FOSTROKE%%%%%")
                        .replace("\\", "%%%%%BACKSLASH%%%%%")
                        .replace("{", "%%%%%OPCURLY%%%%%")
                        .replace("}", "%%%%%CLOCURLY%%%%%")
                        .replace("*", "%%%%%STAR%%%%%")
                        .replace("?", "%%%%%QUESTIONMARK%%%%%")
                        .replace("!", "%%%%%EXCLAMATIONMARK%%%%%")
                        .replace(",", "%%%%%COMMA%%%%%")

                    val folderDetails = tempFolderDetails
                    Log.d(TAG, "fffffffffffolderDetails:" + folderDetails)
                    newestPendingExamUnitsList.add(folderDetails)


                    folderAdapter = FolderAdapter(this, newestPendingExamUnitsList)
                    recyclerView = findViewById(R.id.ExamrecyclerView)
                    recyclerView.layoutManager = GridLayoutManager(this, 2)
                    recyclerView.adapter = folderAdapter

                    progressBar.visibility = View.GONE

                    val StartTimeAndLimitList = mutableListOf(StartTime, TimeLimit, examDate)


                    val KeensharedPreferences = getSharedPreferences("myKeenprefs", Context.MODE_PRIVATE).edit()
                    KeensharedPreferences.putString("KeenFolderDetails", folderDetails).apply()
                    readFireStoreItems(folderDetails)
                    saveStartTimeDateAndLimitList(StartTimeAndLimitList, folderDetails)
                }
            }

            .setNegativeButton("Cancel", null)
            .show()
    }

    // Function to validate the date format
    private fun isValidDate(dateStr: String): Boolean {
        return dateStr.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
    }

    // Function to validate the time format
    private fun isValidTime(timeStr: String): Boolean {
        return timeStr.matches(Regex("\\d{2}:\\d{2}"))
    }


    private fun readPendingExamUnitsListFromFirestore(){
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)){
                val db = FirebaseFirestore.getInstance()
                val PendingExamUnitsListReference = db.collection("ExamMgt").document("PendingExamUnitsList")
                PendingExamUnitsListReference.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val newestDetails = document.data.toString()
                        val newString = newestDetails.substringAfter("PendingExamUnitsList=").substringBefore("}")
                        val items = newString.removeSurrounding("[", "]").split(",")
                        val newestPendingExamUnitsList = items.toMutableList()

                        val btnCreateFolder: Button = findViewById(R.id.btn_create_folder)
                        btnCreateFolder.setOnClickListener {
                            showCreateFolderDialog(newestPendingExamUnitsList)
                        }

                        folderAdapter = FolderAdapter(this, newestPendingExamUnitsList)
                        recyclerView = findViewById(R.id.ExamrecyclerView)
                        recyclerView.layoutManager = GridLayoutManager(this, 2)
                        recyclerView.adapter = folderAdapter

                        progressBar.visibility = View.GONE

                    }
                    else {
                        Toast.makeText(this, "No Exam Has Been Set.", Toast.LENGTH_LONG).show()
                        val btnCreateFolder: Button = findViewById(R.id.btn_create_folder)
                        btnCreateFolder.setOnClickListener {
                            showCreateFolderDialog(newestPendingExamUnitsList)
                        }
                    }
                }
            }
            else{
                Toast.makeText(this, "No Internet connection. Please check and Try Again", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Their Are problems With The Network Configuration. Check With The System Administrator, ", Toast.LENGTH_LONG).show()
        }
    }

    private fun readFireStoreItems(folderDetails: String){
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)){
                val db = FirebaseFirestore.getInstance()
                val PendingExamUnitsListReference = db.collection("ExamMgt").document("PendingExamUnitsList")
                PendingExamUnitsListReference.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val newestDetails = document.data.toString()
                        val newString = newestDetails.substringAfter("PendingExamUnitsList=").substringBefore("}")
                        val items = newString.removeSurrounding("[", "]").split(",")
                        val newestPendingExamUnitsList = items.toMutableList()

                        newestPendingExamUnitsList.add(folderDetails)
                        savePendingExamUnitsList(newestPendingExamUnitsList)
                    }
                    else {
                        val newestPendingExamUnitsList = mutableListOf(folderDetails)
                        savePendingExamUnitsList(newestPendingExamUnitsList)
                    }
                }
            }
            else{
                Toast.makeText(this, "No Internet connection. Please check and Try Again", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Their Are problems With The Network Configuration. Check With The System Administrator, ", Toast.LENGTH_LONG).show()
        }
    }

    private fun savePendingExamUnitsList(newestPendingExamUnitsList: MutableList<String>){
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)){
                val db = FirebaseFirestore.getInstance()
                val pendingExamUnitsListReference = db.collection("ExamMgt").document("PendingExamUnitsList")
                val pendingExamUnitsMap = hashMapOf(
                    "newestPendingExamUnitsList" to newestPendingExamUnitsList
                )

                pendingExamUnitsListReference.set(pendingExamUnitsMap).addOnSuccessListener {
                    Log.d("FirestoreSuccess", "Successfully saved pending exam units list: $newestPendingExamUnitsList")
                }.addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Failed to save pending exam units list, error: ${exception.message}", exception)
                }
            }
            else{
                Toast.makeText(this, "No Internet connection. Please check and Try Again", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Their Are problems With The Network Configuration. Check With The System Administrator, ", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveStartTimeDateAndLimitList(StartTimeAndLimitList: MutableList<String>, folderDetails: String){
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) or capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)){
                val db = FirebaseFirestore.getInstance()
                val StartTimeAndLimitListReference = db.collection("ExamMgt").document(folderDetails).collection("pendingExamStartTimeAndLimitCollection").document("StartTimeAndLimitDocument")
                val StartTimeAndLimitListMap = hashMapOf(
                    "StartTimeAndLimitList" to StartTimeAndLimitList
                )
                StartTimeAndLimitListReference.set(StartTimeAndLimitListMap).addOnSuccessListener {
                    Log.d("FirestoreSuccess", "Successfully saved StartTimeAndLimitList")
                }.addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Failed to save StartTimeAndLimitList, error: ${exception.message}", exception)
                }
            }
            else{
                Toast.makeText(this, "No Internet connection. Internet Connection required to Save Exam Start Time, Date And Limit List. Please check and Try Again", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Their Are problems With The Network Configuration. Try switching your network connection off and on again. If it doesn't work, check With The System Administrator, ", Toast.LENGTH_LONG).show()
        }
    }
}