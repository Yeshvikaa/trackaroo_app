package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.simats.trackaroo.models.ParentLoginRequest
import com.simats.trackaroo.models.ParentLoginResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParentLoginActivity : AppCompatActivity() {

    private lateinit var studentIdEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: AppCompatButton
    private lateinit var forgotPasswordText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_login)

        studentIdEditText = findViewById(R.id.editTextStudentId)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.btnParentLogin)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)

        forgotPasswordText.setOnClickListener {
            startActivity(Intent(this, ParentForgotPasswordActivity::class.java))
        }

        loginButton.setOnClickListener {
            val studentId = studentIdEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (studentId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter Student ID and Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = ParentLoginRequest(student_id = studentId, password = password)

            RetrofitClient.instance.parentLogin(request)
                .enqueue(object : Callback<ParentLoginResponse> {
                    override fun onResponse(
                        call: Call<ParentLoginResponse>,
                        response: Response<ParentLoginResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val res = response.body()!!
                            Toast.makeText(this@ParentLoginActivity, res.message, Toast.LENGTH_SHORT).show()

                            if (res.status == "success") {
                                val intent = Intent(this@ParentLoginActivity, ParentDashboardActivity::class.java)
                                intent.putExtra("student_id", studentId)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Log.e("ParentLogin", "Login failed: ${response.code()} ${response.message()}")
                            Toast.makeText(this@ParentLoginActivity, "Login failed. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ParentLoginResponse>, t: Throwable) {
                        Log.e("ParentLogin", "Network error: ${t.localizedMessage}")
                        Toast.makeText(this@ParentLoginActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
