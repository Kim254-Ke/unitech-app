package com.example.unitech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var roleRadioGroup: RadioGroup
    private var selectedRole: String? = null // To store the selected role

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize FirebaseAuth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize the RadioGroup
        roleRadioGroup = findViewById(R.id.roleRadioGroup)

        // Set a listener for the RadioGroup
        roleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedRole = when (checkedId) {
                R.id.studentRadioButton -> "Student"
                R.id.lecturerRadioButton -> "Lecturer"
                R.id.adminRadioButton -> "Administrator"
                else -> null
            }
        }

        // Set up UI listeners
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email = findViewById<EditText>(R.id.emailInput).text.toString().trim()
            val password = findViewById<EditText>(R.id.passwordInput).text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && selectedRole != null) {
                signInUser(email, password)
            } else {
                Toast.makeText(
                    this,
                    "Please fill in all fields and select a role",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        findViewById<TextView>(R.id.forgotPasswordLink).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        findViewById<TextView>(R.id.signUpLink).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        findViewById<Button>(R.id.resendVerificationButton).setOnClickListener {
            resendVerificationEmail()
        }
    }

    private fun signInUser(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Perform Firebase authentication in the background
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user

                withContext(Dispatchers.Main) {
                    if (user != null && user.isEmailVerified) {
                        checkUserRole(email)
                    } else if (user != null && !user.isEmailVerified) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Please verify your email before logging in.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "User not found. Please sign up.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Invalid credentials. Please check your details.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginError", e.localizedMessage ?: "Unknown error")
                }
            }
        }
    }

    private suspend fun checkUserRole(email: String) {
        try {
            // Query Firestore to get the user's role
            val snapshot = firestore.collection("ExamMgt")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (snapshot.isEmpty) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "User role not found in database.", Toast.LENGTH_SHORT).show()
                }
                return
            }

            val document = snapshot.documents[0]
            val roleInDb = document.getString("role")

            withContext(Dispatchers.Main) {
                if (roleInDb == selectedRole) {
                    Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                    saveEmailToPreferences(email)

                    // Redirect based on role
                    when (selectedRole) {
                        "Student" -> startActivity(Intent(this@LoginActivity, StudentMainActivity::class.java))
                        "Lecturer" -> startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        "Administrator" -> startActivity(Intent(this@LoginActivity, AdminMainActivity::class.java))
                    }
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Selected role does not match the role in the database.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, "Error checking role: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("RoleCheckError", e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun resendVerificationEmail() {
        lifecycleScope.launch(Dispatchers.IO) {
            val user = auth.currentUser
            if (user != null) {
                try {
                    // Perform email verification resend in the background
                    user.sendEmailVerification().await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Verification email sent! Please check your inbox.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Failed to resend verification email. Please try again.", Toast.LENGTH_SHORT).show()
                        Log.e("ResendError", e.localizedMessage ?: "Unknown error")
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "No user logged in to resend email.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveEmailToPreferences(email: String) {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("userEmail", email)
        editor.apply()
    }
}
