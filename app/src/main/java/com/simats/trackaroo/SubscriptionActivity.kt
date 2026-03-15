package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var btnSubscribe: Button
    private lateinit var planGroup: RadioGroup
    private var selectedPlan: String = "Monthly Plan"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        btnSubscribe = findViewById(R.id.btnSubscribe)
        planGroup = findViewById(R.id.planGroup)

        // Handle plan selection
        planGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPlan = when (checkedId) {
                R.id.planMonthly -> "Monthly Plan"
                R.id.planYearly -> "Yearly Plan"
                R.id.planLifetime -> "Lifetime Plan"
                else -> "Monthly Plan"
            }
        }

        // Subscribe button click
        btnSubscribe.setOnClickListener {
            Toast.makeText(this, "Subscribed to $selectedPlan!", Toast.LENGTH_SHORT).show()
            navigateToHomeScreen()
        }
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, HomeScreenActivity::class.java)
        startActivity(intent)
        finish()
    }
}
