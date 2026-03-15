package com.simats.trackaroo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AttendanceScannerActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var scannerBox: PreviewView
    private lateinit var backButton: ImageView
    private lateinit var scanLine: View
    private lateinit var ocrResult: TextView
    private lateinit var overlay: View

    private val scannedIds = mutableSetOf<String>()
    private lateinit var cameraProvider: ProcessCameraProvider
    private var imageAnalyzer: ImageAnalysis? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.attendance_scanner)

        // Views
        scannerBox = findViewById(R.id.scannerBox)
        backButton = findViewById(R.id.backButton)
        scanLine = findViewById(R.id.scanLine)
        ocrResult = findViewById(R.id.ocrResult)
        overlay = findViewById(R.id.overlay)

        cameraExecutor = Executors.newSingleThreadExecutor()
        backButton.setOnClickListener { finish() }

        // Camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            startCamera()
        }

        // Scan line animation
        scannerBox.post { startScanLineAnimation() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(scannerBox.surfaceProvider)
            }

            // The image analyzer is the key to improving stability.
            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analyzer ->
                    analyzer.setAnalyzer(cameraExecutor, TextAnalyzer { text ->
                        runOnUiThread { processScannedText(text) }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        imageAnalyzer?.clearAnalyzer()
        cameraProvider.unbindAll()
    }

    // --- OCR processing (focus only on numeric student ID) ---
    private fun processScannedText(text: String) {
        val cleanedText = text.replace("\n", " ").trim()

        // Find a numeric student ID (adjust regex based on your ID length)
        val regex = Regex("\\b\\d{6,12}\\b")
        val match = regex.find(cleanedText)

        if (match != null) {
            val studentId = match.value

            if (!scannedIds.contains(studentId)) {
                scannedIds.add(studentId)
                ocrResult.text = "Scanning ID: $studentId"
                sendAttendance(studentId, "boarded")
            }
        } else {
            ocrResult.text = "No valid student ID found"
        }
    }

    private fun sendAttendance(studentId: String, status: String) {
        val url = "http://172.23.51.65/trackaroo/scan_student.php"

        val json = JSONObject().apply {
            put("student_id", studentId)
            put("status", status)
        }

        val request = JsonObjectRequest(Request.Method.POST, url, json,
            { response ->
                val statusResp = response.optString("status")
                val message = response.optString("message", "Scanned")

                if (statusResp == "success") {
                    ocrResult.text = "Attendance marked for ID: $studentId"
                    showOverlay(Color.GREEN)
                    Toast.makeText(this, "Attendance marked successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    ocrResult.text = message
                    showOverlay(Color.RED)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                ocrResult.text = "Server error: ${error.message}"
                showOverlay(Color.RED)
                Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun startScanLineAnimation() {
        val animation = TranslateAnimation(
            0f, 0f,
            0f, scannerBox.height.toFloat()
        ).apply {
            duration = 1500
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }
        scanLine.startAnimation(animation)
    }

    private fun showOverlay(color: Int) {
        overlay.setBackgroundColor(color)
        overlay.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            overlay.visibility = View.GONE
        }, 800)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        stopCamera()
    }
}

// --- MODIFIED Text Analyzer ---
private class TextAnalyzer(val listener: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Add a simple delay to prevent processing every single frame
    private var lastAnalyzedTimestamp = 0L

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()

        // Process a new frame only if 500ms have passed since the last one
        // This makes the scanning feel more stable and less "shaky"
        if (currentTimestamp - lastAnalyzedTimestamp < 500) {
            imageProxy.close()
            return
        }
        lastAnalyzedTimestamp = currentTimestamp

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    listener(visionText.text)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}