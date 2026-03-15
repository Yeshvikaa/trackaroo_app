package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat // Import the correct SwitchCompat

class PrivacySettingsActivity : AppCompatActivity() {

    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacy_settings) // Assuming your layout file is named activity_privacy_settings.xml

        // Get the user role from intent
        userRole = intent.getStringExtra("role")

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        // Use SwitchCompat from androidx.appcompat.widget
        val pickupSwitch = findViewById<SwitchCompat>(R.id.pickupSwitch)
        pickupSwitch.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Pickup reminders ON" else "Pickup reminders OFF"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        val changePasswordLayout = findViewById<LinearLayout>(R.id.changePasswordLayout)
        changePasswordLayout.setOnClickListener {
            when (userRole) {
                "student" -> startActivity(Intent(this, StudentChangePasswordActivity::class.java))
                "parent" -> startActivity(Intent(this, ParentChangePasswordActivity::class.java))
                "driver" -> startActivity(Intent(this, DriverChangePasswordActivity::class.java))
                "admin" -> startActivity(Intent(this, AdminChangePasswordActivity::class.java))
                else -> Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show()
            }
        }
    }
}