package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StudentUpdateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_profile_update) // your XML file name

        // Get references to the input fields
        val editStudentId = findViewById<EditText>(R.id.editStudentId)
        val editName = findViewById<EditText>(R.id.editName)
        val editAge = findViewById<EditText>(R.id.editAge)
        val editGrade = findViewById<EditText>(R.id.editGrade)
        val editSchool = findViewById<EditText>(R.id.editSchool)

        // Buttons
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        // Save button logic
        btnSave.setOnClickListener {
            // (Here you can save the data to database or SharedPreferences if needed)

            Toast.makeText(this, "Changes have been saved", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "student") // So settings screen knows to show profile button
            startActivity(intent)
            finish()
        }

        // Cancel button logic
        btnCancel.setOnClickListener {
            Toast.makeText(this, "Changes have been canceled", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "student")
            startActivity(intent)
            finish()
        }
    }
}
