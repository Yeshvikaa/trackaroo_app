package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.DriverOtpResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverOtpActivity : AppCompatActivity() {

    private lateinit var otpInput: EditText
    private lateinit var continueButton: Button
    private lateinit var resendOtp: TextView
    private var driverId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_otp)

        otpInput = findViewById(R.id.driverid)
        continueButton = findViewById(R.id.continueButton)
        resendOtp = findViewById(R.id.resendotp)

        driverId = intent.getStringExtra("driver_id")

        continueButton.setOnClickListener {
            val otp = otpInput.text.toString().trim()
            if (otp.isEmpty()) {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.instance.driverOtp("verify", driverId!!, otp)
                .enqueue(object : Callback<DriverOtpResponse> {
                    override fun onResponse(call: Call<DriverOtpResponse>, response: Response<DriverOtpResponse>) {
                        val body = response.body()
                        if (body?.status == "success") {
                            Toast.makeText(this@DriverOtpActivity, body.message, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@DriverOtpActivity, DriverNewPasswordActivity::class.java)
                            intent.putExtra("driver_id", driverId)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@DriverOtpActivity, body?.message ?: "Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<DriverOtpResponse>, t: Throwable) {
                        Toast.makeText(this@DriverOtpActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        resendOtp.setOnClickListener {
            RetrofitClient.instance.driverOtp("resend", driverId!!)
                .enqueue(object : Callback<DriverOtpResponse> {
                    override fun onResponse(call: Call<DriverOtpResponse>, response: Response<DriverOtpResponse>) {
                        val body = response.body()
                        Toast.makeText(this@DriverOtpActivity, body?.message ?: "Error", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<DriverOtpResponse>, t: Throwable) {
                        Toast.makeText(this@DriverOtpActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
