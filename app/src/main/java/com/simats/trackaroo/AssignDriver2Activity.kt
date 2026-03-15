package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.adapters.DriverAdapter
import com.simats.trackaroo.models.DriverData
import com.simats.trackaroo.models.AssignDriver2Response
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignDriver2Activity : AppCompatActivity() {

    private lateinit var driverAdapter: DriverAdapter
    private var driverList: MutableList<DriverData> = mutableListOf()
    private var allDrivers: MutableList<DriverData> = mutableListOf()
    private var routeNumber: String? = null  // Store route number here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.assign_driver_2)

        val backButton = findViewById<ImageView>(R.id.backButton)
        val searchBar = findViewById<EditText>(R.id.searchDriver)
        val recyclerView = findViewById<RecyclerView>(R.id.driversRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Get route number from AssignDriver1
        routeNumber = intent.getStringExtra("route_number")
        if (routeNumber == null) {
            Toast.makeText(this, "Route not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize adapter with click listener
        driverAdapter = DriverAdapter(driverList) { driver ->
            val intent = Intent(this, AssignDriver3Activity::class.java)
            intent.putExtra("driver_id", driver.driver_id)
            intent.putExtra("driver_name", driver.driver_name)
            intent.putExtra("phone_number", driver.phone_number)

            // ✅ Pass the route number along to AssignDriver3
            routeNumber?.let {
                intent.putExtra("route_number", it)
            }

            startActivity(intent)
        }
        recyclerView.adapter = driverAdapter

        backButton.setOnClickListener { finish() }

        // Fetch drivers for this route
        fetchDrivers(routeNumber!!)

        // 🔍 Local search by driver_id, driver_name, phone_number
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDrivers(s.toString())
            }
        })
    }

    private fun fetchDrivers(routeNumber: String) {
        val api = RetrofitClient.instance
        val body = mapOf("route_number" to routeNumber)

        api.getAssignedDrivers(body).enqueue(object : Callback<AssignDriver2Response> {
            override fun onResponse(
                call: Call<AssignDriver2Response>,
                response: Response<AssignDriver2Response>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    allDrivers.clear()
                    response.body()?.data?.let { allDrivers.addAll(it) }

                    driverList.clear()
                    driverList.addAll(allDrivers)
                    driverAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@AssignDriver2Activity,
                        "No drivers found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AssignDriver2Response>, t: Throwable) {
                Toast.makeText(
                    this@AssignDriver2Activity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterDrivers(query: String) {
        val filtered = if (query.isEmpty()) {
            allDrivers
        } else {
            allDrivers.filter {
                it.driver_id.contains(query, ignoreCase = true) ||
                        it.driver_name.contains(query, ignoreCase = true) ||
                        it.phone_number.contains(query, ignoreCase = true)
            }
        }
        driverList.clear()
        driverList.addAll(filtered)
        driverAdapter.notifyDataSetChanged()
    }
}
