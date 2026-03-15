package com.simats.trackaroo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminEmergencyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_emergency)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener { finish() }

        // Call buttons
        val callPolice: ImageView = findViewById(R.id.callpolice)
        val callAmbulance: ImageView = findViewById(R.id.callambulance)
        val callFire: ImageView = findViewById(R.id.callfireservice)

        callPolice.setOnClickListener { makeCall("100") }
        callAmbulance.setOnClickListener { makeCall("108") }
        callFire.setOnClickListener { makeCall("101") }

        // Quick action buttons
        val sendAlertButton: Button = findViewById(R.id.sendalert)
        val contactDriverButton: Button = findViewById(R.id.contactDriver)
        val addContactsButton: Button = findViewById(R.id.addContacts)

        // ✅ Updated: Open VoiceBotActivity for admin
        sendAlertButton.setOnClickListener {
            val intent = Intent(this, VoiceBotActivity::class.java)
            intent.putExtra("USER_TYPE", "admin")       // specify admin type
            intent.putExtra("USER_ID", "ADMIN123")      // replace with actual admin ID if available
            startActivity(intent)
            // Do NOT call finish() here, keep activity open
        }

        contactDriverButton.setOnClickListener {
            val intent = Intent(this, DriverContactsActivity::class.java)
            startActivity(intent)
        }

        addContactsButton.setOnClickListener {
            val intent = Intent(this, AdminContactsActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to make a call
    private fun makeCall(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot make call: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
