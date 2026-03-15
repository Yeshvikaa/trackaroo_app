package com.simats.trackaroo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.ScaleBarOverlay

class DriverDashboardActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var busMarker: Marker
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val LOCATION_PERMISSION_CODE = 2001
    private var firstFix = true

    // UI elements
    private lateinit var totalStopsTextView: TextView
    private lateinit var busNumberTextView: TextView

    private var driverId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osm_prefs", MODE_PRIVATE)
        )
        setContentView(R.layout.driver_dashboard)

        // -------------------------
        // Initialize UI elements
        // -------------------------
        totalStopsTextView = findViewById(R.id.totalstops)
        busNumberTextView = findViewById(R.id.busNumber)

        // ✅ Get driver_id from intent
        driverId = intent.getStringExtra("driver_id") ?: ""
        if (driverId.isEmpty()) {
            Toast.makeText(this, "Driver ID missing. Please login again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // -------------------------
        // Button navigation
        // -------------------------
        findViewById<ImageView>(R.id.menubutton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java).apply {
                putExtra("userType", "driver")
            })
        }
        findViewById<ImageView>(R.id.start_route).setOnClickListener {
            startActivity(Intent(this, StartRouteActivity::class.java))
        }
        findViewById<ImageView>(R.id.attendance_scanner).setOnClickListener {
            startActivity(Intent(this, AttendanceScannerActivity::class.java))
        }
        findViewById<ImageView>(R.id.driver_navigation_button).setOnClickListener {
            startActivity(Intent(this, DriverNavigationActivity::class.java))
        }
        findViewById<ImageView>(R.id.contact_icon).setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }

        // -------------------------
        // Initialize MapView
        // -------------------------
        mapView = findViewById(R.id.osmMapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        val controller = mapView.controller
        controller.setZoom(18.0)

        // Compass
        val compassOverlay = CompassOverlay(this, InternalCompassOrientationProvider(this), mapView)
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)

        // Scale bar
        val scaleBar = ScaleBarOverlay(mapView)
        scaleBar.setAlignBottom(true)
        mapView.overlays.add(scaleBar)

        // Bus marker (driver's location)
        busMarker = Marker(mapView).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "My Bus"
        }
        mapView.overlays.add(busMarker)

        // -------------------------
        // Initialize location client
        // -------------------------
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Request/check permission
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

        // -------------------------
        // Fetch total stops and bus numbers for this driver
        // -------------------------
        fetchDriverStops(driverId)
    }

    // -------------------------
    // Fetch total stops & bus numbers from backend
    // -------------------------
    private fun fetchDriverStops(driverId: String) {
        val url = "https://172.23.51.65/trackaroo/driver_dashboard.php" // Replace with your PHP URL

        val json = JSONObject()
        json.put("driver_id", driverId)

        val request = JsonObjectRequest(
            Request.Method.POST, url, json,
            { response ->
                if (response.getString("status") == "success") {
                    val totalStops = response.getInt("total_stops")

                    val busArray: JSONArray = response.getJSONArray("bus_numbers")
                    val busList = mutableListOf<String>()
                    for (i in 0 until busArray.length()) {
                        busList.add(busArray.getString(i))
                    }

                    // Update UI
                    totalStopsTextView.text = totalStops.toString()
                    busNumberTextView.text = "Buses: ${busList.joinToString(", ")}"
                } else {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Network error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    // -------------------------
    // Location updates
    // -------------------------
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L
        ).setMinUpdateIntervalMillis(1000L).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val currentPos = GeoPoint(location.latitude, location.longitude)

                if (currentPos.latitude != 0.0 && currentPos.longitude != 0.0) {
                    busMarker.position = currentPos

                    if (firstFix) {
                        mapView.controller.setCenter(currentPos)
                        firstFix = false
                        Toast.makeText(
                            this@DriverDashboardActivity,
                            "GPS fix acquired!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    mapView.invalidate()
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
