package com.simats.trackaroo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.DriverNewPasswordResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverNewPasswordActivity : AppCompatActivity() {

    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var setPasswordButton: Button
    private var driverId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_new_password)

        newPassword = findViewById(R.id.newPassword)
        confirmPassword = findViewById(R.id.confirmPassword)
        setPasswordButton = findViewById(R.id.setPasswordButton)

        // Get driver ID from intent
        driverId = intent.getStringExtra("driver_id")

        if (driverId.isNullOrEmpty()) {
            Toast.makeText(this, "Driver ID not found", Toast.LENGTH_SHORT).show()
            finish() // close activity if driver ID is missing
            return
        }

        setPasswordButton.setOnClickListener {
            val newPass = newPassword.text.toString().trim()
            val confirmPass = confirmPassword.text.toString().trim()

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPass != confirmPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show a progress message
            Toast.makeText(this, "Updating password...", Toast.LENGTH_SHORT).show()

            // Make API call
            RetrofitClient.instance.driverNewPassword(driverId!!, newPass)
                .enqueue(object : Callback<DriverNewPasswordResponse> {
                    override fun onResponse(
                        call: Call<DriverNewPasswordResponse>,
                        response: Response<DriverNewPasswordResponse>
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            Toast.makeText(
                                this@DriverNewPasswordActivity,
                                body?.message ?: "Password updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (body?.status == "success") {
                                finish() // go back to login
                            }
                        } else {
                            Toast.makeText(
                                this@DriverNewPasswordActivity,
                                "Server error: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<DriverNewPasswordResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(
                            this@DriverNewPasswordActivity,
                            "Network error: ${t.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}
