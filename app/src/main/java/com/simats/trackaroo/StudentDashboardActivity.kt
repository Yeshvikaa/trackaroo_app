package com.simats.trackaroo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.simats.trackaroo.models.StudentRouteResponse
import com.simats.trackaroo.network.RetrofitClient
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.ScaleBarOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var busMarker: Marker
    private lateinit var studentMarker: Marker

    private lateinit var greetingText: TextView
    private lateinit var dateText: TextView
    private lateinit var busNumberText: TextView
    private lateinit var etaText: TextView
    private lateinit var pickupTimeText: TextView
    private lateinit var routeText: TextView

    private lateinit var studentId: String
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 5000L // 5 seconds

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val LOCATION_PERMISSION_CODE = 1001

    private var firstFix = true // ✅ Track first GPS fix

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osm_prefs", MODE_PRIVATE)
        )
        setContentView(R.layout.student_dashboard)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Profile icon → User profile
        findViewById<ImageView>(R.id.studentProfileIcon).setOnClickListener {
            startActivity(Intent(this, StudentUserProfileActivity::class.java))
        }

        // Menu icon → Settings page
        findViewById<ImageView>(R.id.menubutton).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "student")
            startActivity(intent)
        }

        // Initialize MapView
        mapView = findViewById(R.id.osmMapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK) // ✅ Show streets, names
        mapView.setMultiTouchControls(true)
        val controller = mapView.controller
        controller.setZoom(18.0) // closer zoom for street level

        // Add compass
        val compassOverlay = CompassOverlay(this, InternalCompassOrientationProvider(this), mapView)
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)

        // Add scale bar
        val scaleBar = ScaleBarOverlay(mapView)
        scaleBar.setAlignBottom(true)
        mapView.overlays.add(scaleBar)

        // Bus marker
        busMarker = Marker(mapView)
        busMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        busMarker.title = "Bus Location"
        mapView.overlays.add(busMarker)

        // Student marker
        studentMarker = Marker(mapView)
        studentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        studentMarker.title = "You"
        mapView.overlays.add(studentMarker)

        // ❌ Removed polyline initialization

        // Initialize TextViews
        greetingText = findViewById(R.id.studentNameTextView)
        dateText = findViewById(R.id.dateTextView)
        busNumberText = findViewById(R.id.busNumberTextView)
        etaText = findViewById(R.id.etaTextView)
        pickupTimeText = findViewById(R.id.pickupTimeTextView)
        routeText = findViewById(R.id.routeTextView)

        // Get student ID
        studentId = intent.getStringExtra("student_id") ?: run {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else {
            startLocationUpdates()
        }

        // Start updating bus location periodically
        startLiveUpdates()
    }

    private fun startLiveUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                fetchStudentRouteData(studentId)
                handler.postDelayed(this, refreshInterval)
            }
        })
    }

    private fun fetchStudentRouteData(studentId: String) {
        val apiService = RetrofitClient.instance
        val body = mapOf("student_id" to studentId)

        apiService.getStudentRoute(body).enqueue(object : Callback<StudentRouteResponse> {
            override fun onResponse(
                call: Call<StudentRouteResponse>,
                response: Response<StudentRouteResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val data = response.body()!!.data

                    greetingText.text = "Hi, ${data?.student_name}"
                    dateText.text = data?.date
                    busNumberText.text = data?.bus_number
                    etaText.text = "ETA: ${data?.eta_minutes_left} mins"
                    pickupTimeText.text = data?.pickup_time
                    routeText.text = data?.route

                    val lat = data?.latitude
                    val lon = data?.longitude
                    if (lat != null && lon != null) {
                        val newPos = GeoPoint(lat, lon)
                        busMarker.position = newPos
                        mapView.invalidate()
                    }

                } else {
                    Toast.makeText(
                        this@StudentDashboardActivity,
                        "Student or Route not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<StudentRouteResponse>, t: Throwable) {
                Toast.makeText(
                    this@StudentDashboardActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // 🔹 Continuous Location Updates
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000 // update every 2 seconds
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val currentPos = GeoPoint(location.latitude, location.longitude)

                // Update student marker
                studentMarker.position = currentPos

                // ✅ First GPS fix → move map to your location
                if (firstFix) {
                    mapView.controller.setCenter(currentPos)
                    firstFix = false
                }

                mapView.invalidate()
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // ❌ Removed updateRouteLine function

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback) // stop updates when paused
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Stop bus updates
        fusedLocationClient.removeLocationUpdates(locationCallback) // Stop GPS updates
    }
}
