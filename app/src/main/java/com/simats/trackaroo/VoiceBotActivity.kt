package com.simats.trackaroo

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.simats.trackaroo.models.EmergencyRequest
import com.simats.trackaroo.models.EmergencyResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoiceBotActivity : AppCompatActivity() {

    // -------------------- UI ELEMENTS --------------------
    private lateinit var resultText: TextView
    private lateinit var micButton: ImageView
    private lateinit var sendButton: Button
    private val REQ_CODE_SPEECH_INPUT = 100
    private var spokenMessage = ""

    // -------------------- EMERGENCY KEYWORDS --------------------
    // All possible English emergency keywords
    private val emergencyKeywords = listOf(
        "fire", "help", "accident", "danger", "emergency", "injury",
        "police", "ambulance", "alert", "rescue", "hazard", "attack", "bomb"
    )

    // Tamil to English mapping for emergency words (common variations included)
    private val tamilToEnglishMap = mapOf(
        "தீ" to "fire",
        "பயர்" to "fire",
        "எமர்ஜென்சி" to "emergency",
        "உதவி" to "help",
        "ஹெல்ப்" to "help",
        "விபத்து" to "accident",
        "அபாயம்" to "danger",
        "அவசரம்" to "emergency",
        "காயம்" to "injury",
        "போலீஸ்" to "police",
        "ஆம்புலன்ஸ்" to "ambulance",
        "எச்சரிக்கை" to "alert",
        "உடனடி உதவி" to "rescue",
        "ஆபத்து" to "hazard",
        "அட்டென்ஷன்" to "attention",
        "தாக்கு" to "attack",
        "பாம்பு" to "bomb",
        "பாம்" to "bomb"
    )

    // -------------------- USER INFO --------------------
    private var userType = "driver"
    private var userId = "DRIVER123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_bot)

        // Get user info from intent
        userType = intent.getStringExtra("USER_TYPE") ?: "driver"
        userId = intent.getStringExtra("USER_ID") ?: "DRIVER123"

        // Initialize UI elements
        resultText = findViewById(R.id.resultText)
        micButton = findViewById(R.id.micButton)
        sendButton = findViewById(R.id.sendButton)
        sendButton.isEnabled = false

        micButton.setOnClickListener { startVoiceInput() }
        sendButton.setOnClickListener { sendAlert() }

        // Start polling alerts (optional if needed)
        pollAlerts()
    }

    // -------------------- VOICE INPUT --------------------
    private fun startVoiceInput() {
        val intent = Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, "ta-IN") // Tamil
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak your emergency...")

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(this, "Speech not supported", Toast.LENGTH_SHORT).show()
        }
    }

    // -------------------- HANDLE VOICE RESULT --------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
            spokenMessage = result?.get(0) ?: ""
            resultText.text = spokenMessage
            sendButton.isEnabled = spokenMessage.isNotEmpty()
        }
    }

    // -------------------- SEND ALERT --------------------
    private fun sendAlert() {
        if (spokenMessage.isEmpty()) {
            Toast.makeText(this, "Please speak first!", Toast.LENGTH_SHORT).show()
            return
        }

        // 1️⃣ Translate Tamil words to English
        var translatedMessage = spokenMessage
        for ((tamil, english) in tamilToEnglishMap) {
            if (spokenMessage.contains(tamil)) {
                translatedMessage = translatedMessage.replace(tamil, english)
            }
        }

        val translatedMessageLower = translatedMessage.lowercase()

        // 2️⃣ Check if the message contains any emergency keyword
        val isEmergency = emergencyKeywords.any { translatedMessageLower.contains(it) }

        // 3️⃣ If emergency detected, play sound + show notification
        if (isEmergency) {
            val mp = MediaPlayer.create(this, R.raw.emergency)
            mp.start()
            mp.setOnCompletionListener { it.release() }

            showLocalNotification("Emergency detected: $translatedMessage")
        }

        // 4️⃣ Send alert to backend
        val request = EmergencyRequest(
            user_type = userType,
            user_id = userId,
            message = translatedMessage,
            route_number = "R101" // dynamic if needed
        )

        RetrofitClient.instance.sendEmergency(request).enqueue(object : Callback<EmergencyResponse> {
            override fun onResponse(call: Call<EmergencyResponse>, response: Response<EmergencyResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@VoiceBotActivity, "Alert sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@VoiceBotActivity, "Failed to send alert", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmergencyResponse>, t: Throwable) {
                Toast.makeText(this@VoiceBotActivity, "Failed to send alert: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // -------------------- POLL ALERTS (OPTIONAL) --------------------
    private fun pollAlerts() {
        val handler = Handler(mainLooper)
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchAlerts()
                handler.postDelayed(this, 5000)
            }
        }, 5000)
    }

    private fun fetchAlerts() {
        // Optional: fetch alerts logic if you need live notifications from backend
    }

    // -------------------- LOCAL NOTIFICATION --------------------
    private fun showLocalNotification(message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "emergency_alert_channel"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Emergency Alerts", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_alert)
            .setContentTitle("From School")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
