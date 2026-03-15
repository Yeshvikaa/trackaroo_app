package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SignupRoleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_role)

        val studentSignup = findViewById<ImageView>(R.id.student_signup)
        val parentSignup = findViewById<ImageView>(R.id.parent_signup)
        val driverSignup = findViewById<ImageView>(R.id.driver_signup)
        val adminSignup = findViewById<ImageView>(R.id.admin_signup)

        studentSignup.setOnClickListener {
            startActivity(Intent(this, StudentSignupActivity::class.java))
        }

        parentSignup.setOnClickListener {
            startActivity(Intent(this, ParentSignupActivity::class.java))
        }

        driverSignup.setOnClickListener {
            startActivity(Intent(this, DriverSignupActivity::class.java))
        }

        adminSignup.setOnClickListener {
            startActivity(Intent(this, AdminSignupActivity::class.java))
        }
    }
}
