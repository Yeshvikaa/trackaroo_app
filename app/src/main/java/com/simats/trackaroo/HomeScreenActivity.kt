package com.simats.trackaroo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen) // Make sure homescreen.xml exists

        // Initialize views
        val trackButton = findViewById<Button>(R.id.track_button)
        val signupLink = findViewById<TextView>(R.id.signup_link)

        // Button to go to LoginRoleActivity
        trackButton.setOnClickListener {
            startActivity(Intent(this, LoginRoleActivity::class.java))
        }

        // Link to go to SignupRoleActivity
        signupLink.setOnClickListener {
            startActivity(Intent(this, SignupRoleActivity::class.java))
        }

        // Handle deep link if app opened via QR
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null) {
            val scheme = data.scheme  // "trackaroo"
            val host = data.host      // "open"

            // Only respond to trackaroo://open
            if (scheme == "trackaroo" && host == "open") {
                // App opened from QR → stay on HomeScreenActivity
                // Optionally, you could auto-navigate somewhere if needed
                // Example: startActivity(Intent(this, StudentDashboardActivity::class.java))
            }
        }
    }
}
