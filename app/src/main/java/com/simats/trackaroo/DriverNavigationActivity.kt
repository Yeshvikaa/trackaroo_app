package com.simats.trackaroo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.adapters.DriverRouteAdapter
import com.simats.trackaroo.models.Route
import com.simats.trackaroo.models.DriverNavigationResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverNavigationActivity : AppCompatActivity() {

    private lateinit var driversRecyclerView: RecyclerView
    private lateinit var adapter: DriverRouteAdapter
    private val routeList = ArrayList<Route>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_navigation)

        driversRecyclerView = findViewById(R.id.driversRecyclerView)
        driversRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DriverRouteAdapter(routeList)
        driversRecyclerView.adapter = adapter

        fetchDriverRoutes("R102") // Replace with dynamic route_number if needed
    }

    private fun fetchDriverRoutes(route_number: String) {
        val requestBody = mapOf("route_number" to route_number)

        val call = RetrofitClient.instance.getDriverNavigation(requestBody)
        call.enqueue(object : Callback<DriverNavigationResponse> {
            override fun onResponse(
                call: Call<DriverNavigationResponse>,
                response: Response<DriverNavigationResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    response.body()?.data?.let { route ->
                        routeList.clear()
                        routeList.add(route) // Add single route object
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(
                        this@DriverNavigationActivity,
                        response.body()?.message ?: "No data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<DriverNavigationResponse>, t: Throwable) {
                Toast.makeText(this@DriverNavigationActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
