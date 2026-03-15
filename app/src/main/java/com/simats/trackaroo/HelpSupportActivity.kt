package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HelpSupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help_support)

        val backBtn = findViewById<ImageView>(R.id.backButton)

        backBtn.setOnClickListener {
            // Navigate to SettingsActivity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish() // optional: to close current screen
        }
    }
}

