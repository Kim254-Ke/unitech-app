package com.example.unitech

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExamQuestionsAdapter(private val context: Context, private val questionNumbers: MutableList<String>, private val UnitDetails: String) : RecyclerView.Adapter<ExamQuestionsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val number_card: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question_answer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = questionNumbers[position]
        holder.number_card.text = number

        holder.number_card.setOnClickListener{
            val intent = Intent(context, QuestionsActivity::class.java)
            intent.putExtra("UnitDetails", UnitDetails)
            intent.putExtra("questionNumber", number)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return questionNumbers.size
    }

    fun updateNumbers(questionNumbers: MutableList<String>) {
        notifyItemInserted(questionNumbers.size - 1)
    }
}
