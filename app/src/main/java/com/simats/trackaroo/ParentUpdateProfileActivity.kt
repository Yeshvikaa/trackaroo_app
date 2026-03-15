package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ParentUpdateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_update_profile) // your parent XML file name

        // Input fields
        val editStudentId = findViewById<EditText>(R.id.studentid)
        val editStudentName = findViewById<EditText>(R.id.studentname)
        val editParentEmail = findViewById<EditText>(R.id.parentemail)
        val editParentPhone = findViewById<EditText>(R.id.parentphone)

        // Buttons
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener {
            // Save logic here
            Toast.makeText(this, "Parent profile updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "parent")
            startActivity(intent)
            finish()
        }

        btnCancel.setOnClickListener {
            Toast.makeText(this, "Changes canceled", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "parent")
            startActivity(intent)
            finish()
        }
    }
}
