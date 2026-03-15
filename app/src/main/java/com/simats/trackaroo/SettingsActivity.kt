package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        // Get user type passed from previous screen
        val userType = intent.getStringExtra("userType")

        // Buttons
        val btnUpdateProfile = findViewById<Button>(R.id.updateProfileBtn)
        val btnNotifications = findViewById<Button>(R.id.notificationBtn)
        val btnPrivacy = findViewById<Button>(R.id.privacyBtn)
        val btnHelpSupport = findViewById<Button>(R.id.helpSupportBtn)
        val btnLogout = findViewById<Button>(R.id.logoutbutton)

        // ✅ Show Update Profile for all user types
        btnUpdateProfile.visibility = Button.VISIBLE

        // ✅ Update Profile click based on role
        btnUpdateProfile.setOnClickListener {
            val intent = when (userType) {
                "student" -> Intent(this, StudentUpdateProfileActivity::class.java)
                "parent" -> Intent(this, ParentUpdateProfileActivity::class.java)
                "driver" -> Intent(this, DriverUpdateProfileActivity::class.java)
                "admin" -> Intent(this, AdminUpdateProfileActivity::class.java)
                else -> null
            }
            intent?.let { startActivity(it) }
        }

        btnNotifications.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        btnPrivacy.setOnClickListener {
            startActivity(Intent(this, PrivacySettingsActivity::class.java))
        }

        btnHelpSupport.setOnClickListener {
            startActivity(Intent(this, HelpSupportActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val intent = when (userType) {
                "student" -> Intent(this, StudentLoginActivity::class.java)
                "parent" -> Intent(this, ParentLoginActivity::class.java)
                "driver" -> Intent(this, DriverLoginActivity::class.java)
                "admin" -> Intent(this, AdminLoginActivity::class.java)
                else -> Intent(this, LoginRoleActivity::class.java)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
