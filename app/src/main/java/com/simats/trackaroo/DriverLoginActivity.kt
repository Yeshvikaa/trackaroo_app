package com.simats.trackaroo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.DriverLoginRequest
import com.simats.trackaroo.models.DriverLoginResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverLoginActivity : AppCompatActivity() {

    private lateinit var driverId: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPassword: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_login)

        driverId = findViewById(R.id.logindriverid)
        password = findViewById(R.id.loginpassword)
        loginButton = findViewById(R.id.driverlogin)
        forgotPassword = findViewById(R.id.forgotPasswordText)

        sharedPreferences = getSharedPreferences("DriverPrefs", MODE_PRIVATE)

        // Forgot password → open Forgot Password page
        forgotPassword.setOnClickListener {
            startActivity(Intent(this, DriverForgotPasswordActivity::class.java))
        }

        // Login button
        loginButton.setOnClickListener {
            if (driverId.text.isBlank() || password.text.isBlank()) {
                Toast.makeText(this, "Please enter Driver ID and Password", Toast.LENGTH_SHORT).show()
            } else {
                sendLoginRequest()
            }
        }
    }

    private fun sendLoginRequest() {
        val request = DriverLoginRequest(
            driver_id = driverId.text.toString().trim(),
            password = password.text.toString().trim()
        )

        RetrofitClient.instance.driverLogin(request)
            .enqueue(object : Callback<DriverLoginResponse> {
                override fun onResponse(
                    call: Call<DriverLoginResponse>,
                    response: Response<DriverLoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        Toast.makeText(
                            this@DriverLoginActivity,
                            res?.message ?: "Unknown response",
                            Toast.LENGTH_LONG
                        ).show()

                        if (res?.status == "success") {
                            val driverIdValue = driverId.text.toString().trim()

                            // ✅ Save login status
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isDriverSignedUp", true)
                            editor.putString("driver_id", driverIdValue)
                            editor.apply()

                            // ✅ Redirect to Driver Dashboard with driver_id
                            val intent = Intent(this@DriverLoginActivity, DriverDashboardActivity::class.java)
                            intent.putExtra("driver_id", driverIdValue)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@DriverLoginActivity,
                            "Server error: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DriverLoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@DriverLoginActivity,
                        "Failed: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
