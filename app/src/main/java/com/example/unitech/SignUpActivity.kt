package com.example.unitech

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize FirebaseAuth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            val email = findViewById<EditText>(R.id.emailInput).text.toString().trim()
            val password = findViewById<EditText>(R.id.passwordInput).text.toString().trim()
            val confirmPassword = findViewById<EditText>(R.id.confirmPasswordInput).text.toString().trim()

            val selectedRoleId = findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId
            val role = if (selectedRoleId != -1) {
                findViewById<RadioButton>(selectedRoleId).text.toString()
            } else {
                null
            }

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && role != null) {
                if (password == confirmPassword) {
                    signUpUser(email, password, role)
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields and select a role", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpUser(email: String, password: String, role: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Create a new user with email and password
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user != null) {
                    // Save email and role in Firestore under "ExamMgt" collection
                    saveUserToFirestore(user.uid, email, role)

                    // Send verification email
                    try {
                        user.sendEmailVerification().await()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Sign-Up Successful! Please check your email to verify your account.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (verificationException: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Failed to send verification email. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun saveUserToFirestore(uid: String, email: String, role: String) {
        try {
            val userMap = hashMapOf(
                "email" to email,
                "role" to role)

            firestore.collection("ExamMgt").document(uid).set(userMap).await()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@SignUpActivity, "Failed to save user to Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
