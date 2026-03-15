package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminUpdateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_update_profile) // your admin XML file name

        // Input fields
        val editAdminId = findViewById<EditText>(R.id.adminid)
        val editAdminEmail = findViewById<EditText>(R.id.adminemail)
        val editAdminPhone = findViewById<EditText>(R.id.adminphone)

        // Buttons
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener {
            // Save logic here
            Toast.makeText(this, "Admin profile updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "admin")
            startActivity(intent)
            finish()
        }

        btnCancel.setOnClickListener {
            Toast.makeText(this, "Changes canceled", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "admin")
            startActivity(intent)
            finish()
        }
    }
}
