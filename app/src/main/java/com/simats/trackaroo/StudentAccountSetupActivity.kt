package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.simats.trackaroo.models.StudentAccountSetupRequest
import com.simats.trackaroo.models.StudentAccountSetupResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentAccountSetupActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    private lateinit var confirmButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_account_setup)

        emailField = findViewById(R.id.studentEmail)
        passwordField = findViewById(R.id.studentPassword)
        confirmPasswordField = findViewById(R.id.studentConfirmPassword)
        confirmButton = findViewById(R.id.confirmbtn)

        val studentId = intent.getStringExtra("student_id") ?: ""

        confirmButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            // ✅ Frontend validations
            when {
                email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                }

                !email.endsWith("@gmail.com") -> {
                    Toast.makeText(this, "Only Gmail addresses are allowed", Toast.LENGTH_SHORT).show()
                }

                password != confirmPassword -> {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }

                !password.contains(Regex("[!@#\$%^&*(),.?\":{}|<>]")) -> {
                    Toast.makeText(this, "Password must contain a special character", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    sendAccountSetupRequest(studentId, email, password, confirmPassword)
                }
            }
        }
    }

    private fun sendAccountSetupRequest(
        studentId: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val request = StudentAccountSetupRequest(
            student_id = studentId,
            student_email = email,
            password = password,
            confirm_password = confirmPassword
        )

        RetrofitClient.instance.studentAccountSetup(request)
            .enqueue(object : Callback<StudentAccountSetupResponse> {
                override fun onResponse(
                    call: Call<StudentAccountSetupResponse>,
                    response: Response<StudentAccountSetupResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val res = response.body()!!
                        Toast.makeText(this@StudentAccountSetupActivity, res.message, Toast.LENGTH_LONG).show()

                        if (res.status == "success") {
                            val intent = Intent(this@StudentAccountSetupActivity, StudentLoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@StudentAccountSetupActivity, "Setup failed!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StudentAccountSetupResponse>, t: Throwable) {
                    Toast.makeText(this@StudentAccountSetupActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
