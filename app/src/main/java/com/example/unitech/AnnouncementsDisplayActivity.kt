package com.example.unitech

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AnnouncementsDisplayActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_announcements)

        val db = FirebaseFirestore.getInstance()
        val announcementName = (intent.getStringExtra("announcementName")).toString()
        val studentCategory = (intent.getStringExtra("studentCategory")).toString()
        val btnSave: Button = findViewById(R.id.btn_save)

        btnSave.setOnClickListener {
            val announcementEditText: EditText = findViewById(R.id.editText)
            val AnnouncementEditText = announcementEditText.text.toString()

            lifecycleScope.launch(Dispatchers.IO) {
                val announcementReference = db. collection("Announcements"). document(studentCategory)
                val announcementMap = hashMapOf(
                    "AnnouncementTitle" to announcementName,
                    "AnnouncementItSELF" to AnnouncementEditText
                )

                try {
                    announcementReference.set(announcementMap).await()
                    Log.d("successful", "SUCCESSFUL")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AnnouncementsDisplayActivity, "Announcement saved successfully.", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception) {
                    Log.e("Failed", "Failed: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AnnouncementsDisplayActivity, "Failed to save announcement: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }

    }
}
