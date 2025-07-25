package com.example.unitech

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class Tab3Fragment(private val unitDetails: String, private val questionNumber: String) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab3, container, false)

        val db = FirebaseFirestore.getInstance()

        val checkBoxRule1: CheckBox = view.findViewById(R.id.checkBoxRule1)
        val checkBoxRule2: CheckBox = view.findViewById(R.id.checkBoxRule2)
        val checkBoxRule3: CheckBox = view.findViewById(R.id.checkBoxRule3)
        val checkBoxRule4: CheckBox = view.findViewById(R.id.checkBoxRule4)
        val checkBoxRule5: CheckBox = view.findViewById(R.id.checkBoxRule5)
        val buttonSubmit: Button = view.findViewById(R.id.buttonSubmitRules)

        // Reference to the exam marking rules document
        val examMarkingRulesReference = db.collection("ExamMgt")
            .document(unitDetails)
            .collection("examMarkingRules")
            .document(questionNumber)

        // Fetch and display the saved rules if available
        examMarkingRulesReference.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val rulesString = document.getString("stringedExamMarkingRulesList")
                    if (!rulesString.isNullOrEmpty()) {
                        // Remove brackets and whitespace then split by comma.
                        // Example string: "[Rule 1, Rule 3]" becomes a list: ["Rule 1", "Rule 3"]
                        val rulesList = rulesString
                            .removePrefix("[")
                            .removeSuffix("]")
                            .split(",")
                            .map { it.trim() }

                        // Check each rule based on what is saved
                        if (rulesList.contains("Rule 1")) checkBoxRule1.isChecked = true
                        if (rulesList.contains("Rule 2")) checkBoxRule2.isChecked = true
                        if (rulesList.contains("Rule 3")) checkBoxRule3.isChecked = true
                        if (rulesList.contains("Rule 4")) checkBoxRule4.isChecked = true
                        if (rulesList.contains("Rule 5")) checkBoxRule5.isChecked = true
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load saved rules", Toast.LENGTH_SHORT).show()
            }

        buttonSubmit.setOnClickListener {
            // Create a list for selected rules
            val examMarkingRulesList = mutableListOf<String>()
            if (checkBoxRule1.isChecked) examMarkingRulesList.add("Rule 1")
            if (checkBoxRule2.isChecked) examMarkingRulesList.add("Rule 2")
            if (checkBoxRule3.isChecked) examMarkingRulesList.add("Rule 3")
            if (checkBoxRule4.isChecked) examMarkingRulesList.add("Rule 4")
            if (checkBoxRule5.isChecked) examMarkingRulesList.add("Rule 5")

            if (examMarkingRulesList.isEmpty()) {
                Toast.makeText(requireContext(), "No rules selected", Toast.LENGTH_LONG).show()
            } else {
                // Convert the list to string format (you can also store as an array in Firestore)
                val stringedExamMarkingRulesList = examMarkingRulesList.toString()
                val examMarkingRulesMap = hashMapOf("stringedExamMarkingRulesList" to stringedExamMarkingRulesList)

                examMarkingRulesReference.set(examMarkingRulesMap)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Exam Marking Rules Saved successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed To Save Exam Marking Rules, error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        return view
    }
}
