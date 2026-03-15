package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.simats.trackaroo.models.AdminLoginRequest
import com.simats.trackaroo.models.AdminLoginResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var adminIdEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: AppCompatButton
    private lateinit var forgotPasswordText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_login)

        // Bind views
        adminIdEditText = findViewById(R.id.editTextAdminId)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.adminlogin)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)

        // Forgot password click
        forgotPasswordText.setOnClickListener {
            startActivity(Intent(this, AdminForgotPasswordActivity::class.java))
        }

        // Login button click
        loginButton.setOnClickListener {
            val adminId = adminIdEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (adminId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter Admin ID and Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = AdminLoginRequest(admin_id = adminId, password = password)

            RetrofitClient.instance.adminLogin(request)
                .enqueue(object : Callback<AdminLoginResponse> {
                    override fun onResponse(
                        call: Call<AdminLoginResponse>,
                        response: Response<AdminLoginResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val res = response.body()!!
                            Toast.makeText(this@AdminLoginActivity, res.message, Toast.LENGTH_SHORT).show()

                            if (res.status == "success") {
                                val intent = Intent(this@AdminLoginActivity, AdminDashboardActivity::class.java)
                                intent.putExtra("admin_id", res.admin_id)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Log.e("AdminLogin", "Login failed: ${response.code()} ${response.message()}")
                            Toast.makeText(this@AdminLoginActivity, "Login failed. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<AdminLoginResponse>, t: Throwable) {
                        Log.e("AdminLogin", "Network error: ${t.localizedMessage}")
                        Toast.makeText(this@AdminLoginActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
