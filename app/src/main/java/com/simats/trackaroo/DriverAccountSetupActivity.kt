package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.DriverAccountSetupRequest
import com.simats.trackaroo.models.DriverAccountSetupResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverAccountSetupActivity : AppCompatActivity() {

    private lateinit var etDriverId: EditText
    private lateinit var etDriverEmail: EditText
    private lateinit var btnSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_account_setup)

        // Bind views
        etDriverId = findViewById(R.id.editDriverId)
        etDriverEmail = findViewById(R.id.editDriverEmail)
        btnSignup = findViewById(R.id.btnsignup)

        btnSignup.setOnClickListener {
            val driverId = etDriverId.text.toString().trim()
            val driverEmail = etDriverEmail.text.toString().trim()

            when {
                driverId.isEmpty() -> {
                    Toast.makeText(this, "Driver ID is required", Toast.LENGTH_SHORT).show()
                }
                driverEmail.isEmpty() -> {
                    Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
                }
                !Patterns.EMAIL_ADDRESS.matcher(driverEmail).matches() -> {
                    Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    setupAccount(driverId, driverEmail)
                }
            }
        }

        // Prefill values from signup (optional)
        val driverIdExtra = intent.getStringExtra("driver_id")
        etDriverId.setText(driverIdExtra ?: "")
    }

    private fun setupAccount(driverId: String, driverEmail: String) {
        val request = DriverAccountSetupRequest(driverId, driverEmail)

        RetrofitClient.instance.driverAccountSetup(request)
            .enqueue(object : Callback<DriverAccountSetupResponse> {
                override fun onResponse(
                    call: Call<DriverAccountSetupResponse>,
                    response: Response<DriverAccountSetupResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            Toast.makeText(
                                this@DriverAccountSetupActivity,
                                body.message,
                                Toast.LENGTH_LONG
                            ).show()

                            if (body.status == "success") {
                                // ✅ Redirect to DriverLoginActivity
                                val intent = Intent(
                                    this@DriverAccountSetupActivity,
                                    DriverLoginActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(
                                this@DriverAccountSetupActivity,
                                "Empty response from server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@DriverAccountSetupActivity,
                            "Server error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DriverAccountSetupResponse>, t: Throwable) {
                    Toast.makeText(
                        this@DriverAccountSetupActivity,
                        "Failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
