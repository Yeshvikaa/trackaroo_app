package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StudentChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_change_password)

        val changeButton = findViewById<Button>(R.id.changeButton)
        changeButton.setOnClickListener {
            val intent = Intent(this, PrivacySettingsActivity::class.java)
            intent.putExtra("role", "student") // Pass role again
            startActivity(intent)
            finish()
        }
    }
}
