package com.simats.trackaroo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import android.widget.Toast

class EditRoutesActivity : AppCompatActivity() {

    private lateinit var routeNumberEdit: EditText
    private lateinit var routePathEdit: EditText
    private lateinit var routeTimeEdit: EditText
    private lateinit var busNumberEdit: EditText
    private lateinit var saveButton: AppCompatButton
    private lateinit var cancelButton: AppCompatButton
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_routes)

        // Initialize views
        routeNumberEdit = findViewById(R.id.routeNumberEdit)
        routePathEdit = findViewById(R.id.routePathEdit)
        routeTimeEdit = findViewById(R.id.routeTimeEdit)
        busNumberEdit = findViewById(R.id.busNumberEdit)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        backButton = findViewById(R.id.backButton)

        // Get data from intent
        val routeNumber = intent.getStringExtra("routeNumber") ?: ""
        val routePath = intent.getStringExtra("routePath") ?: ""
        val routeTime = intent.getStringExtra("routeTime") ?: ""
        val busNumber = intent.getStringExtra("busNumber") ?: ""

        // Pre-fill fields
        routeNumberEdit.setText(routeNumber)
        routePathEdit.setText(routePath)
        routeTimeEdit.setText(routeTime)
        busNumberEdit.setText(busNumber)

        // Back
        backButton.setOnClickListener { finish() }

        // Cancel
        cancelButton.setOnClickListener { finish() }

        // Save
        saveButton.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("routeNumber", routeNumberEdit.text.toString())
                putExtra("routePath", routePathEdit.text.toString())
                putExtra("routeTime", routeTimeEdit.text.toString())
                putExtra("busNumber", busNumberEdit.text.toString())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, "Route updated!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
