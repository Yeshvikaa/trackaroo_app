package com.simats.trackaroo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginRoleActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var isDriverSignedUp: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_role)

        sharedPreferences = getSharedPreferences("DriverPrefs", MODE_PRIVATE)

        val studentIcon = findViewById<ImageView>(R.id.student)
        val parentIcon = findViewById<ImageView>(R.id.parent)
        val driverIcon = findViewById<ImageView>(R.id.driver)
        val adminIcon = findViewById<ImageView>(R.id.admin)
        val emergencyIcon = findViewById<ImageView>(R.id.emergency)

        // Navigate to respective login activities
        studentIcon.setOnClickListener {
            startActivity(Intent(this, StudentLoginActivity::class.java))
        }
        parentIcon.setOnClickListener {
            startActivity(Intent(this, ParentLoginActivity::class.java))
        }
        driverIcon.setOnClickListener {
            startActivity(Intent(this, DriverLoginActivity::class.java))
        }
        adminIcon.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }

        // Emergency button click
        emergencyIcon.setOnClickListener {
            if (isDriverSignedUp) {
                startActivity(Intent(this, VoiceBotActivity::class.java))
            } else {
                Toast.makeText(
                    this,
                    "Only signed-up drivers can access emergency!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh driver login status every time the activity is resumed
        isDriverSignedUp = sharedPreferences.getBoolean("isDriverSignedUp", false)
    }
}
