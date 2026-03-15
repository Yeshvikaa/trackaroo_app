package com.simats.trackaroo

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.adapters.DriverAdapter
import com.simats.trackaroo.models.AssignDriver3Response
import com.simats.trackaroo.models.DriverData
import com.simats.trackaroo.network.RetrofitClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignDriver3Activity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var assignButton: Button

    private var driverList: MutableList<DriverData> = mutableListOf()
    private lateinit var driverAdapter: DriverAdapter

    private var routeNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.assign_driver_3)

        backButton = findViewById(R.id.backButton)
        recyclerView = findViewById(R.id.driversRecyclerView)
        assignButton = findViewById(R.id.assigndriver)

        routeNumber = intent.getStringExtra("route_number")

        recyclerView.layoutManager = LinearLayoutManager(this)
        driverAdapter = DriverAdapter(driverList) { driver ->
            // When a driver card is clicked, we can highlight or select it
            selectedDriver = driver
            Toast.makeText(this, "${driver.driver_name} selected", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = driverAdapter

        backButton.setOnClickListener { finish() }

        // Load drivers dynamically if you want; here we just pass one driver from intent
        intent.getStringExtra("driver_id")?.let { id ->
            val name = intent.getStringExtra("driver_name") ?: ""
            val phone = intent.getStringExtra("phone_number") ?: ""
            driverList.add(DriverData(id, name, phone))
            driverAdapter.notifyDataSetChanged()
        }

        assignButton.setOnClickListener {
            selectedDriver?.let { driver ->
                assignDriverToRoute(driver.driver_id)
            } ?: run {
                Toast.makeText(this, "Please select a driver", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var selectedDriver: DriverData? = null

    private fun assignDriverToRoute(driverId: String) {
        if (routeNumber == null) {
            Toast.makeText(this, "Route not found", Toast.LENGTH_SHORT).show()
            return
        }

        val body = mapOf(
            "driver_id" to driverId,
            "route_number" to routeNumber
        )

        RetrofitClient.instance.assignDriverToRoute(body)
            .enqueue(object : Callback<AssignDriver3Response> { // ✅ use the typed response
                override fun onResponse(
                    call: Call<AssignDriver3Response>,
                    response: Response<AssignDriver3Response>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        Toast.makeText(this@AssignDriver3Activity, res?.message, Toast.LENGTH_SHORT).show()
                        if (res?.status == "success") finish()
                    } else {
                        Toast.makeText(this@AssignDriver3Activity, "API Error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AssignDriver3Response>, t: Throwable) {
                    Toast.makeText(this@AssignDriver3Activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

    }
}
