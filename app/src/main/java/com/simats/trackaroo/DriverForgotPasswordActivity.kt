package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.DriverForgotPasswordResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverForgotPasswordActivity : AppCompatActivity() {

    private lateinit var driverId: EditText
    private lateinit var driverEmail: EditText
    private lateinit var sendOtpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_forgot_password)

        driverId = findViewById(R.id.driverid)
        driverEmail = findViewById(R.id.driveremail)
        sendOtpButton = findViewById(R.id.sendotpButton)

        sendOtpButton.setOnClickListener {
            val id = driverId.text.toString().trim()
            val email = driverEmail.text.toString().trim()

            // Check if fields are empty after trimming spaces
            if (id.isEmpty()) {
                driverId.error = "Driver ID is required"
                driverId.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                driverEmail.error = "Driver Email is required"
                driverEmail.requestFocus()
                return@setOnClickListener
            }

            // Optional: Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                driverEmail.error = "Enter a valid email"
                driverEmail.requestFocus()
                return@setOnClickListener
            }

            val request = mapOf("driver_id" to id, "driver_email" to email)

            RetrofitClient.instance.driverForgotPassword(request)
                .enqueue(object : Callback<DriverForgotPasswordResponse> {
                    override fun onResponse(
                        call: Call<DriverForgotPasswordResponse>,
                        response: Response<DriverForgotPasswordResponse>
                    ) {
                        val body = response.body()
                        if (body?.status == "success") {
                            Toast.makeText(this@DriverForgotPasswordActivity, body.message, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@DriverForgotPasswordActivity, DriverOtpActivity::class.java)
                            intent.putExtra("driver_id", id)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@DriverForgotPasswordActivity, body?.message ?: "Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<DriverForgotPasswordResponse>, t: Throwable) {
                        Toast.makeText(this@DriverForgotPasswordActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
