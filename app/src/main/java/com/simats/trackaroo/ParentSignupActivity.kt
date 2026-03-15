package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.simats.trackaroo.models.ParentSignupRequest
import com.simats.trackaroo.models.ParentSignupResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParentSignupActivity : AppCompatActivity() {

    private lateinit var studentIdInput: EditText
    private lateinit var studentName: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signupButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_signup)

        // Login text
        val loginText = findViewById<TextView>(R.id.loginText)
        val htmlString = "Already a member? <font color='#1E40AF'><u><b>login</b></u></font>"
        loginText.text = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
        loginText.setOnClickListener {
            startActivity(Intent(this, ParentLoginActivity::class.java))
        }

        // Bind views
        studentIdInput = findViewById(R.id.studentId)
        studentName = findViewById(R.id.studentname)
        email = findViewById(R.id.parentEmail)
        phone = findViewById(R.id.parentPhone)
        password = findViewById(R.id.parentPassword)
        confirmPassword = findViewById(R.id.parentConfirmPassword)
        signupButton = findViewById(R.id.confirmbtn)

        signupButton.setOnClickListener {
            sendParentSignupRequest()
        }
    }

    private fun sendParentSignupRequest() {
        val sid = studentIdInput.text.toString().trim()
        val nm = studentName.text.toString().trim()
        val em = email.text.toString().trim()
        val ph = phone.text.toString().trim()
        val pw = password.text.toString()
        val cpw = confirmPassword.text.toString()

        Log.d("SignupDebug", "SID: '$sid', Name: '$nm', Email: '$em', Phone: '$ph', PW: '$pw', CPW: '$cpw'")

        // Validate input
        when {
            sid.isEmpty() -> { Toast.makeText(this, "Please enter Student ID", Toast.LENGTH_SHORT).show(); return }
            nm.isEmpty() -> { Toast.makeText(this, "Please enter Student Name", Toast.LENGTH_SHORT).show(); return }
            em.isEmpty() -> { Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show(); return }
            ph.isEmpty() -> { Toast.makeText(this, "Please enter Phone Number", Toast.LENGTH_SHORT).show(); return }
            pw.isEmpty() -> { Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show(); return }
            cpw.isEmpty() -> { Toast.makeText(this, "Please confirm Password", Toast.LENGTH_SHORT).show(); return }
        }

        // Email must be Gmail
        if (!em.endsWith("@gmail.com") || !android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
            Toast.makeText(this, "Enter a valid Gmail address", Toast.LENGTH_SHORT).show()
            return
        }

        // Password must have at least one special character
        if (!pw.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*"))) {
            Toast.makeText(this, "Password must contain at least one special character", Toast.LENGTH_SHORT).show()
            return
        }

        // Password match
        if (pw != cpw) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Phone number max 10 digits
        if (ph.length > 10) {
            Toast.makeText(this, "Phone number must not exceed 10 digits", Toast.LENGTH_SHORT).show()
            return
        }

        // Create request body — keys match PHP backend
        val requestBody = ParentSignupRequest(
            student_id = sid,
            student_name = nm,
            parent_email = em,
            phone_number = ph,
            password = pw,
            confirm_password = cpw
        )

        Log.d("ParentSignup", "Sending request: $requestBody")

        // Retrofit call
        RetrofitClient.instance.parentSignup(requestBody)
            .enqueue(object : Callback<ParentSignupResponse> {
                override fun onResponse(
                    call: Call<ParentSignupResponse>,
                    response: Response<ParentSignupResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val res = response.body()!!
                        Log.d("ParentSignup", "Response: ${res.status} - ${res.message}")
                        Toast.makeText(this@ParentSignupActivity, res.message, Toast.LENGTH_SHORT).show()

                        // Check status string from PHP backend
                        if (res.status == "success") {
                            startActivity(Intent(this@ParentSignupActivity, ParentLoginActivity::class.java))
                            finish()
                        }
                    } else {
                        Log.e("ParentSignup", "Unsuccessful: ${response.code()} ${response.message()}")
                        Toast.makeText(this@ParentSignupActivity, "Signup failed! Try again.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ParentSignupResponse>, t: Throwable) {
                    Log.e("ParentSignup", "Network Error: ${t.localizedMessage}")
                    Toast.makeText(this@ParentSignupActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
