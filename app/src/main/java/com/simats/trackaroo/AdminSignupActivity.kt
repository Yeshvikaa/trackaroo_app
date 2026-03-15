package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.AdminSignupRequest
import com.simats.trackaroo.models.AdminSignupResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminSignupActivity : AppCompatActivity() {

    private lateinit var adminId: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var adminEmail: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signupButton: Button
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_signup)

        // Bind UI
        adminId = findViewById(R.id.adminId)
        phoneNumber = findViewById(R.id.adminPhone)
        adminEmail = findViewById(R.id.adminEmail)
        password = findViewById(R.id.adminPassword)
        confirmPassword = findViewById(R.id.adminConfirmPassword)
        signupButton = findViewById(R.id.signupButton)
        loginText = findViewById(R.id.loginText)

        // Login Text Clickable
        val htmlString = "Already a member? <font color='#1E40AF'><u><b>login</b></u></font>"
        loginText.text = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
        loginText.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }

        // Confirm button action
        signupButton.setOnClickListener {
            if (validateInputs()) {
                sendSignupRequest()
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (adminId.text.isBlank() || phoneNumber.text.isBlank() ||
            adminEmail.text.isBlank() || password.text.isBlank() || confirmPassword.text.isBlank()
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.text.toString() != confirmPassword.text.toString()) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(adminEmail.text.toString().trim()).matches()) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun sendSignupRequest() {
        val requestBody = AdminSignupRequest(
            admin_id = adminId.text.toString().trim(),
            phone_number = phoneNumber.text.toString().trim(),
            admin_email = adminEmail.text.toString().trim(),
            password = password.text.toString().trim(),
            confirm_password = confirmPassword.text.toString().trim()
        )

        RetrofitClient.instance.adminSignup(requestBody)
            .enqueue(object : Callback<AdminSignupResponse> {
                override fun onResponse(
                    call: Call<AdminSignupResponse>,
                    response: Response<AdminSignupResponse>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        Toast.makeText(
                            this@AdminSignupActivity,
                            res?.message ?: "Signup successful",
                            Toast.LENGTH_LONG
                        ).show()

                        if (res?.status == "success") {
                            // Redirect to AdminLoginActivity (or any other screen)
                            startActivity(Intent(this@AdminSignupActivity, AdminLoginActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@AdminSignupActivity,
                            "Server error: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AdminSignupResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminSignupActivity,
                        "Failed: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
