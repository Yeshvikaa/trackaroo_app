package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.AdminDashboardResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var busesText: TextView
    private lateinit var studentsText: TextView
    private lateinit var routesText: TextView
    private lateinit var driversText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_dashboard)

        // Dashboard counters
        busesText = findViewById(R.id.buses)
        studentsText = findViewById(R.id.students)
        routesText = findViewById(R.id.routes)
        driversText = findViewById(R.id.drivers)

        // Buttons
        val manageRoutesBtn = findViewById<Button>(R.id.manageRoutesBtn)
        val driverManagementBtn = findViewById<Button>(R.id.driverManagementBtn)
        val studentDbBtn = findViewById<Button>(R.id.studentDbBtn)
        val emergencyBtn = findViewById<Button>(R.id.emergencyBtn)
        val menuButton = findViewById<ImageView>(R.id.menubutton)

        // Navigation buttons
        manageRoutesBtn.setOnClickListener { startActivity(Intent(this, ManageRoutesActivity::class.java)) }
        driverManagementBtn.setOnClickListener { startActivity(Intent(this, DriverManagementActivity::class.java)) }
        studentDbBtn.setOnClickListener { startActivity(Intent(this, StudentDatabaseActivity::class.java)) }
        emergencyBtn.setOnClickListener { startActivity(Intent(this, AdminEmergencyActivity::class.java)) }

        // Menu button -> Settings
        menuButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("userType", "admin")
            startActivity(intent)
        }

        // Fetch dashboard data from PHP
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        val call = RetrofitClient.instance.getAdminDashboard()
        call.enqueue(object : Callback<AdminDashboardResponse> {
            override fun onResponse(
                call: Call<AdminDashboardResponse>,
                response: Response<AdminDashboardResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    response.body()?.data?.let { data ->
                        // Show only numbers on cards
                        busesText.text = "Buses: ${data.buses}"
                        studentsText.text = "Students: ${data.students}"
                        routesText.text = "Routes: ${data.routes}"
                        driversText.text = "Drivers: ${data.drivers}"
                    }
                } else {
                    Toast.makeText(this@AdminDashboardActivity, "Failed to load dashboard", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AdminDashboardResponse>, t: Throwable) {
                Toast.makeText(this@AdminDashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
