package com.simats.trackaroo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.simats.trackaroo.models.StudentLoginRequest
import com.simats.trackaroo.models.AuthResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_login)

        val studentIdEt = findViewById<EditText>(R.id.studentIdEditText)
        val passwordEt = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<AppCompatButton>(R.id.studentlogin)
        val forgotPassword = findViewById<TextView>(R.id.forgotPasswordText)

        // Forgot Password navigation
        forgotPassword.setOnClickListener {
            startActivity(Intent(this, StudentForgotPasswordActivity::class.java))
        }

        // Login button click
        loginButton.setOnClickListener {
            val studentId = studentIdEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (studentId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter student ID and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginStudent(studentId, password)
        }
    }

    private fun loginStudent(studentId: String, password: String) {
        val request = StudentLoginRequest(studentId, password)
        val call = RetrofitClient.instance.studentLogin(request)

        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    Log.d("StudentLogin", "Response: $loginResponse")

                    if (loginResponse?.status == "success") {
                        Toast.makeText(this@StudentLoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()

                        // Save login details in SharedPreferences
                        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("student_id", loginResponse.student_id)
                            putString("student_name", loginResponse.name)
                            apply()
                        }

                        // Pass student_id to Dashboard via Intent
                        val intent = Intent(this@StudentLoginActivity, StudentDashboardActivity::class.java)
                        intent.putExtra("student_id", loginResponse.student_id)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@StudentLoginActivity, loginResponse?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@StudentLoginActivity, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@StudentLoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("StudentLogin", "Network error", t)
            }
        })
    }
}
