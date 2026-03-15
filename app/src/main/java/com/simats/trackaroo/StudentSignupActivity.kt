package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.simats.trackaroo.models.StudentSignupRequest
import com.simats.trackaroo.models.StudentSignupResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentSignupActivity : AppCompatActivity() {

    private lateinit var studentId: EditText
    private lateinit var name: EditText
    private lateinit var age: EditText
    private lateinit var grade: EditText
    private lateinit var school: EditText
    private lateinit var address: EditText
    private lateinit var signupButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_signup)

        // Initialize views
        studentId = findViewById(R.id.studentId)
        name = findViewById(R.id.studentName)
        age = findViewById(R.id.studentAge)
        grade = findViewById(R.id.studentGrade)
        school = findViewById(R.id.studentSchool)
        address = findViewById(R.id.studentAddress)
        signupButton = findViewById(R.id.signupButton)

        signupButton.setOnClickListener {
            sendSignupRequest()
        }
    }

    private fun sendSignupRequest() {
        val id = studentId.text.toString().trim()
        val nm = name.text.toString().trim()
        val ag = age.text.toString().toIntOrNull() ?: 0
        val gr = grade.text.toString().trim()
        val sc = school.text.toString().trim()
        val ad = address.text.toString().trim()

        // Validation
        if (id.isEmpty() || nm.isEmpty() || ag <= 0 || gr.isEmpty() || sc.isEmpty() || ad.isEmpty()) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = StudentSignupRequest(
            student_id = id,
            name = nm,
            age = ag,
            grade = gr,
            school = sc,
            address = ad
        )

        Log.d("SignupDebug", "Sending signup request: $requestBody")

        RetrofitClient.instance.studentSignup(requestBody)
            .enqueue(object : Callback<StudentSignupResponse> {
                override fun onResponse(
                    call: Call<StudentSignupResponse>,
                    response: Response<StudentSignupResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val res = response.body()!!
                        Log.d("SignupDebug", "Response: ${res.status} - ${res.message}")

                        Toast.makeText(this@StudentSignupActivity, res.message, Toast.LENGTH_SHORT).show()

                        if (res.status.equals("success", ignoreCase = true)) {
                            // ✅ Pass the actual student_id from backend if available
                            val nextId = res.student_id ?: id

                            val intent = Intent(
                                this@StudentSignupActivity,
                                StudentAccountSetupActivity::class.java
                            )
                            intent.putExtra("student_id", nextId)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Log.e("SignupDebug", "Unsuccessful: ${response.code()} ${response.message()}")
                        Toast.makeText(
                            this@StudentSignupActivity,
                            "Signup failed! Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StudentSignupResponse>, t: Throwable) {
                    Log.e("SignupDebug", "Network Error: ${t.localizedMessage}")
                    Toast.makeText(
                        this@StudentSignupActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
