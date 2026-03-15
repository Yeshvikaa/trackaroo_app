package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.DriverSignupRequest
import com.simats.trackaroo.models.DriverSignupResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverSignupActivity : AppCompatActivity() {

    private lateinit var driverId: EditText
    private lateinit var driverName: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var licenseNumber: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var confirmButton: Button
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_signup)

        // Bind UI
        driverId = findViewById(R.id.driverId)
        driverName = findViewById(R.id.driverName)
        phoneNumber = findViewById(R.id.phoneNumber)
        licenseNumber = findViewById(R.id.licenseNumber)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)
        confirmButton = findViewById(R.id.confirmButton)
        loginText = findViewById(R.id.loginText)

        // Make login text clickable
        val htmlString =
            "Already a member? <font color='#1E40AF'><u><b>login</b></u></font>"
        loginText.text = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
        loginText.setOnClickListener {
            startActivity(Intent(this, DriverLoginActivity::class.java))
        }

        // Confirm button click
        confirmButton.setOnClickListener {
            if (validateInputs()) {
                sendSignupRequest()
            }
        }
    }

    private fun validateInputs(): Boolean {
        return if (driverId.text.isBlank() || driverName.text.isBlank() || phoneNumber.text.isBlank()
            || licenseNumber.text.isBlank() || password.text.isBlank() || confirmPassword.text.isBlank()
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            false
        } else if (password.text.toString() != confirmPassword.text.toString()) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            false
        } else true
    }

    private fun sendSignupRequest() {
        val requestBody = DriverSignupRequest(
            driver_id = driverId.text.toString().trim(),
            driver_name = driverName.text.toString().trim(),
            phone_number = phoneNumber.text.toString().trim(),
            license_number = licenseNumber.text.toString().trim(),
            password = password.text.toString().trim(),
            confirm_password = confirmPassword.text.toString().trim()
        )

        RetrofitClient.instance.driverSignup(requestBody)
            .enqueue(object : Callback<DriverSignupResponse> {
                override fun onResponse(
                    call: Call<DriverSignupResponse>,
                    response: Response<DriverSignupResponse>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        Toast.makeText(
                            this@DriverSignupActivity,
                            res?.message ?: "Signup successful",
                            Toast.LENGTH_LONG
                        ).show()

                        if (res?.status == "success") {
                            // ✅ Redirect to DriverAccountSetupActivity instead of login
                            val intent = Intent(
                                this@DriverSignupActivity,
                                DriverAccountSetupActivity::class.java
                            )
                            // Pass signup data if needed
                            intent.putExtra("driver_id", driverId.text.toString().trim())
                            intent.putExtra("driver_name", driverName.text.toString().trim())
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@DriverSignupActivity,
                            "Server error: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DriverSignupResponse>, t: Throwable) {
                    Toast.makeText(
                        this@DriverSignupActivity,
                        "Failed: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
