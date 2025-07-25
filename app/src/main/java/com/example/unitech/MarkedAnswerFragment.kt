package com.example.unitech

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView

class MarkedAnswerFragment(private var studentAnswer: String?, private val resultList: String?) : Fragment() {
    private lateinit var tempResultList: String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_marked_answer, container, false)

        if (!resultList.isNullOrEmpty()){
            tempResultList = resultList.filter { it !in listOf('[', ']') }


            // Split the string into individual elements
            val elements = tempResultList.split(", ").map { it.trim() }

            // Result list to hold the nested lists
            val finalResult = mutableListOf<List<Any>>()

            // Temporary list to hold items for each sublist
            var tempList = mutableListOf<Any>()

            // Iterate through each element
            for (element in elements) {
                if (element.all { it.isDigit() }) { // Check if the element is a number
                    tempList.add(element.toInt()) // Convert to Int and add to tempList
                    Log.d("tempList", "tempList: $tempList")
                    if (tempList.size == 3) {
                        finalResult.add(tempList) // Add completed sublist to the result
                        Log.d("finalResult11111", "finalResult1111111: $finalResult")
                        tempList = mutableListOf() // Reset tempList for the next group
                    }
                } else { // If it's not a number, assume it's a string
                    tempList.add(element) // Add the string to tempList
                    Log.d("tempList", "tempList: $tempList")
                }
            }

            Log.d("finalResult", "finalResult: $finalResult")
            val spannableString = SpannableString(studentAnswer)
            for (value in finalResult){
                if (value[0] == "correct"){
                    applyHighlight(spannableString, value[1], value[2], Color.GREEN) // Highlights "is" in green
                }
                else if(value[0] == "incorrect"){
                    applyHighlight(spannableString, value[1], value[2], Color.RED) // Highlights "is" in red
                }
                else{
                    Toast.makeText(context, "AI response during marking was invalid. Contact the developer. He should check the AI prompt, re-edit it and fix it.", Toast.LENGTH_LONG).show()
                }
            }

            Log.d("spannableString", "spannableString: $spannableString")
            val markedAnswerContentSection: TextView = view.findViewById(R.id.markedAnswerContent)
            markedAnswerContentSection.text = spannableString

        }
        else{
            Toast.makeText(context, "resultList is either null or empty. check with the developer to fix the issue.", Toast.LENGTH_LONG).show()
        }

        return view
    }

    private fun applyHighlight(
        spannableString: SpannableString,
        start: Any,
        end: Any,
        color: Int
    ) {
        // Create a ForegroundColorSpan with the specified color
        val colorSpan = ForegroundColorSpan(color)
        // Apply the color span to the specified range
        spannableString.setSpan(colorSpan, start as Int, end as Int, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}