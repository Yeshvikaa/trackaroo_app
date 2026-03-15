package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ParentNewPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_new_password)
        val setPasswordButton = findViewById<Button>(R.id.setPasswordButton)
        setPasswordButton.setOnClickListener {
            val intent = Intent(this, ParentLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
