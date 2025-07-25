package com.example.unitech

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(val context: Context, private val notesList: MutableList<String>) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>(){

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteEditText: EditText = itemView.findViewById(R.id.note_edit_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.noteEditText.setText(notesList[position])

        val string = "[a,b,c]"
        val cleanString = string.trim().removeSurrounding("[", "]")
        val elements = cleanString.split(",")
        Log.d("elements", "elements: $elements")
        for (i in elements){
            Log.d("iiiiiiiiii", "iiiiiii: $i")
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

}
