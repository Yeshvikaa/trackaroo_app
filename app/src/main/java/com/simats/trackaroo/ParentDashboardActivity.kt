package com.simats.trackaroo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.ParentDashboardRequest
import com.simats.trackaroo.models.ParentDashboardResponse
import com.simats.trackaroo.network.RetrofitClient
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParentDashboardActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var greetingStudent: TextView
    private lateinit var busNumberText: TextView
    private lateinit var etaText: TextView
    private lateinit var pickupTimeText: TextView
    private lateinit var routeText: TextView
    private lateinit var driverNameText: TextView
    private lateinit var driverPhoneText: TextView
    private lateinit var callIcon: ImageView
    private lateinit var menuIcon: ImageView
    private lateinit var searchStudent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OSMDroid config
        Configuration.getInstance()
            .load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        setContentView(R.layout.parent_dashboard)

        // Initialize views
        mapView = findViewById(R.id.osmMapView)
        greetingStudent = findViewById(R.id.greetingStudent)
        busNumberText = findViewById(R.id.busNumberText)
        etaText = findViewById(R.id.etaText)
        pickupTimeText = findViewById(R.id.pickupTimeText)
        routeText = findViewById(R.id.routeText)
        driverNameText = findViewById(R.id.driverNameText)
        driverPhoneText = findViewById(R.id.driverPhoneText)
        callIcon = findViewById(R.id.callIcon)
        menuIcon = findViewById(R.id.menuIcon)
        searchStudent = findViewById(R.id.searchStudent)

        // Map setup
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(15.0)
        mapController.setCenter(GeoPoint(13.0827, 80.2707)) // Example: Chennai

        // Menu click
        menuIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "parent")
            startActivity(intent)
        }

        // Search bar listener
        searchStudent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val studentId = s.toString().trim()
                if (studentId.isNotEmpty()) {
                    fetchParentDashboard(studentId)
                }
            }
        })
    }

    private fun fetchParentDashboard(studentId: String) {
        val api = RetrofitClient.instance
        api.getParentDashboard(ParentDashboardRequest(studentId))
            .enqueue(object : Callback<ParentDashboardResponse> {
                override fun onResponse(
                    call: Call<ParentDashboardResponse>,
                    response: Response<ParentDashboardResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.data != null) {
                            val data = body.data

                            // Update UI
                            greetingStudent.text = "Hi, ${data.student_name}"
                            busNumberText.text = "Bus: ${data.bus_number}"
                            etaText.text = "ETA: ${data.eta_minutes_left} min"
                            pickupTimeText.text = data.pickup_time
                            routeText.text = data.route
                            driverNameText.text = data.driver_name
                            driverPhoneText.text = data.driver_phone

                            // Call driver
                            callIcon.setOnClickListener {
                                val intent = Intent(Intent.ACTION_DIAL)
                                intent.data = Uri.parse("tel:${data.driver_phone}")
                                startActivity(intent)
                            }

                            // Optional: update map marker if bus GPS available
                            // val busPoint = GeoPoint(data.bus_latitude, data.bus_longitude)
                            // mapView.controller.setCenter(busPoint)

                        } else {
                            Toast.makeText(
                                this@ParentDashboardActivity,
                                "No details found for ID: $studentId",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ParentDashboardActivity,
                            "API failed: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ParentDashboardResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ParentDashboardActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
