package com.simats.trackaroo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class EmergencyChatActivity : AppCompatActivity() {

    private lateinit var voiceResult: TextView
    private lateinit var voiceButton: Button
    private val REQUEST_CODE_SPEECH = 100
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_chat)

        voiceResult = findViewById(R.id.voice_result)
        voiceButton = findViewById(R.id.voice_button)

        voiceButton.setOnClickListener {
            startVoiceRecognition()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Speak your driver ID and emergency message..."
            )
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported!", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                val spokenText = result[0]
                translateToEnglish(spokenText) // 🔹 Translate and then send
            }
        }
    }

    private fun translateToEnglish(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://api.mymemory.translated.net/get?q=${text}&langpair=auto|en"

                val request = Request.Builder()
                    .url(url)
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()

                val translated = body?.substringAfter("\"translatedText\":\"")
                    ?.substringBefore("\"") ?: "Translation failed"

                withContext(Dispatchers.Main) {
                    voiceResult.text = translated
                }

                // 🔹 After translation, send to backend
                sendMessageToBackend(translated)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EmergencyChatActivity,
                        "Translation error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun sendMessageToBackend(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 🔹 Point this to your PHP file that saves + pushes notification
                val url = "http://your-server-ip/send_emergency.php"

                // TODO: Replace driver_id with actual spoken or stored driver id
                val json = """
                    {
                        "driver_id": "12345",
                        "message": "$message"
                    }
                """.trimIndent()

                val body = json.toRequestBody("application/json".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EmergencyChatActivity,
                            "🚨 Emergency message sent & notification triggered!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@EmergencyChatActivity,
                            "Failed to send message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EmergencyChatActivity,
                        "Error sending message: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
