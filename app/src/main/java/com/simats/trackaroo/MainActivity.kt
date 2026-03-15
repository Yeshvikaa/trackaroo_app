package com.simats.trackaroo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_signup)

        // Handle QR deep link if app opened via https:// URL
        handleDeepLink(intent?.data)

        // Existing login text
        val loginText = findViewById<TextView>(R.id.loginText)
        val htmlString = "Already a member? <font color='#1E40AF'><u><b>login</b></u></font>"
        loginText.text = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
        loginText.isClickable = true
        loginText.setOnClickListener {
            val intent = Intent(this, StudentLoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent?.data)
    }

    private fun handleDeepLink(data: Uri?) {
        data?.let {
            val scheme = it.scheme  // should be "https"
            val host = it.host      // your ngrok host
            val path = it.path      // e.g., "/open"

            // Check if this matches your QR deep link
            if (scheme == "https" && host == "unpulleyed-nondeviously-criselda.ngrok-free.dev" && path == "/open") {
                // Open Student Dashboard when QR scanned
                val dashIntent = Intent(this, StudentDashboardActivity::class.java)
                startActivity(dashIntent)
            }
        }
    }
}
