package com.example.unitech

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TeacherAnnouncementsActivity : AppCompatActivity() {

    private var Announcemets_list = mutableListOf<String>()
    private var studentCategory: String = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var teacherAnnouncementsAdapter: TeacherAnnouncementsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_announcements)

        recyclerView = findViewById(R.id.announcements_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        teacherAnnouncementsAdapter = TeacherAnnouncementsAdapter(Announcemets_list, studentCategory)
        recyclerView.adapter = teacherAnnouncementsAdapter

        val make_card_announcements: CardView = findViewById(R.id.make_announcement)
        make_card_announcements.setOnClickListener {
            showCreateFolderDialog()
        }
    }

    private fun showCreateFolderDialog() {
        // Create EditText fields programmatically
        val editTextTitle = EditText(this)
        editTextTitle.hint = "Enter Title"
        val category = EditText(this)
        category.hint = "Enter Category"

        // Create a LinearLayout and add EditTexts
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(editTextTitle)
        layout.addView(category)

        // Build the AlertDialog
        AlertDialog.Builder(this)
            .setTitle("Announcement Title")
            .setView(layout)
            .setPositiveButton("Create") { dialog, which ->
                val announcementName = editTextTitle.text.toString()
                val announcementGroup = category.text.toString()

                Announcemets_list.add(announcementName)
                studentCategory = announcementGroup
                teacherAnnouncementsAdapter.notifyDataSetChanged()

            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    inner class TeacherAnnouncementsAdapter(private val Announcemets_list: MutableList<String>, private val studentCategory: String) :
        RecyclerView.Adapter<TeacherAnnouncementsAdapter.AnnouncementsViewHolder>() {

        inner class AnnouncementsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardAnnouncement: CardView = itemView.findViewById(R.id.card_announcement)
            val displayTextAnnouncements: TextView = itemView.findViewById(R.id.display_text_announcements)

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.t_announcements_list, parent, false)
            return AnnouncementsViewHolder(view)

        }

        override fun onBindViewHolder(holder: AnnouncementsViewHolder, position: Int) {
            val announcementName = Announcemets_list[position]
            holder.displayTextAnnouncements.text = announcementName

            holder.cardAnnouncement.setOnClickListener {
                val intent = Intent(it.context, AnnouncementsDisplayActivity::class.java)
                intent.putExtra("announcementName",announcementName)
                intent.putExtra("studentCategory",studentCategory)
                it.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return Announcemets_list.size
        }
    }
}

