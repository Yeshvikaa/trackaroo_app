package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DriverUpdateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_update_profile) // your driver XML file name

        // Input fields
        val editDriverId = findViewById<EditText>(R.id.driverid)
        val editDriverName = findViewById<EditText>(R.id.drivername)
        val editDriverEmail = findViewById<EditText>(R.id.driveremail)
        val editDriverPhone = findViewById<EditText>(R.id.driverphone)
        val editDriverLicense = findViewById<EditText>(R.id.driverlicense)

        // Buttons
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener {
            // Save logic here
            Toast.makeText(this, "Driver profile updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "driver")
            startActivity(intent)
            finish()
        }

        btnCancel.setOnClickListener {
            Toast.makeText(this, "Changes canceled", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "driver")
            startActivity(intent)
            finish()
        }
    }
}
