package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.adapters.AssignDriver1Adapter
import com.simats.trackaroo.models.AssignDriver1Response
import com.simats.trackaroo.models.Route
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignDriverActivity1 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AssignDriver1Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.assign_driver_1)

        // Back button
        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener { finish() }

        recyclerView = findViewById(R.id.routesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchAssignDriverRoutes()
    }

    private fun fetchAssignDriverRoutes() {
        RetrofitClient.instance.getAssignDriverRoutes().enqueue(object : Callback<AssignDriver1Response> {
            override fun onResponse(
                call: Call<AssignDriver1Response>,
                response: Response<AssignDriver1Response>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val routes: List<Route> = response.body()!!.routes

                    // ✅ Pass click listener to adapter
                    adapter = AssignDriver1Adapter(routes) { route ->
                        val intent = Intent(this@AssignDriverActivity1, AssignDriver2Activity::class.java)
                        intent.putExtra("route_number", route.route_number)
                        intent.putExtra("bus_number", route.bus_number)
                        intent.putExtra("time", route.time)
                        intent.putExtra("route", route.route)
                        startActivity(intent)
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@AssignDriverActivity1, "No routes found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AssignDriver1Response>, t: Throwable) {
                Toast.makeText(this@AssignDriverActivity1, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
