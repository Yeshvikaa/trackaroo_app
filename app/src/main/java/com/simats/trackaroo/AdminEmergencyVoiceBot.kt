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

class AdminEmergencyVoiceBot : AppCompatActivity() {

    private lateinit var voiceResult: TextView
    private lateinit var voiceButton: Button
    private val REQUEST_CODE_SPEECH = 100
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_emergency) // Updated layout name

        voiceResult = findViewById(R.id.voice_result)
        voiceButton = findViewById(R.id.voice_button) // Make sure your button ID matches XML

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
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your emergency message and target role...")
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
                translateToEnglish(spokenText)
            }
        }
    }

    private fun translateToEnglish(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://api.mymemory.translated.net/get?q=${text}&langpair=auto|en"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val body = response.body?.string()

                val translated = body?.substringAfter("\"translatedText\":\"")
                    ?.substringBefore("\"") ?: "Translation failed"

                withContext(Dispatchers.Main) {
                    voiceResult.text = translated
                }

                val role = when {
                    translated.contains("student", true) -> "student"
                    translated.contains("parent", true) -> "parent"
                    translated.contains("driver", true) -> "driver"
                    else -> "all"
                }

                sendMessageToBackend(translated, role)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AdminEmergencyVoiceBot,
                        "Translation error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun sendMessageToBackend(message: String, role: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "http://your-server-ip/send_emergency.php"

                val json = """
                    {
                        "driver_id": "ADMIN",
                        "message": "$message",
                        "role": "$role"
                    }
                """.trimIndent()

                val body = json.toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder().url(url).post(body).build()
                val response = client.newCall(request).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AdminEmergencyVoiceBot,
                            "🚨 Emergency message sent & notification triggered!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@AdminEmergencyVoiceBot,
                            "Failed to send message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AdminEmergencyVoiceBot,
                        "Error sending message: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
