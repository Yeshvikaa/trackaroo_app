package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat

class NotificationsActivity : AppCompatActivity() {

    private lateinit var busDelaySwitch: SwitchCompat
    private lateinit var pickupSwitch: SwitchCompat
    private lateinit var alertsSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications)

        busDelaySwitch = findViewById(R.id.busDelaySwitch)
        pickupSwitch = findViewById(R.id.pickupSwitch)
        alertsSwitch = findViewById(R.id.alertsSwitch)

        fun setupSwitch(switch: SwitchCompat) {
            switch.setOnCheckedChangeListener { _, isChecked ->
                switch.trackTintList = ContextCompat.getColorStateList(
                    this,
                    if (isChecked) R.color.green else android.R.color.darker_gray
                )
            }

            // Set initial track color
            switch.trackTintList = ContextCompat.getColorStateList(
                this,
                if (switch.isChecked) R.color.green else android.R.color.darker_gray
            )
        }

        setupSwitch(busDelaySwitch)
        setupSwitch(pickupSwitch)
        setupSwitch(alertsSwitch)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}
