package com.simats.trackaroo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.adapters.RouteAdapter
import com.simats.trackaroo.models.ManageRoutesResponse
import com.simats.trackaroo.models.Route
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageRoutesActivity : AppCompatActivity() {

    private lateinit var routesRecyclerView: RecyclerView
    private lateinit var routeAdapter: RouteAdapter
    private var routeList: MutableList<Route> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_routes)

        val backButton = findViewById<ImageView>(R.id.backButton)
        val addNewRouteBtn = findViewById<AppCompatButton>(R.id.addnewroutebtn)
        val assignDriverBtn = findViewById<AppCompatButton>(R.id.assigndriverbtn)

        routesRecyclerView = findViewById(R.id.routesRecyclerView)
        routesRecyclerView.layoutManager = LinearLayoutManager(this)

        routeAdapter = RouteAdapter(routeList,
            onEdit = { route -> Toast.makeText(this, "Edit ${route.route_number}", Toast.LENGTH_SHORT).show() },
            onDelete = { route -> Toast.makeText(this, "Delete ${route.route_number}", Toast.LENGTH_SHORT).show() }
        )
        routesRecyclerView.adapter = routeAdapter

        backButton.setOnClickListener { finish() }
        addNewRouteBtn.setOnClickListener {
            startActivity(Intent(this, AddRoutesActivity::class.java))
        }
        assignDriverBtn.setOnClickListener {
            startActivity(Intent(this, AssignDriverActivity1::class.java))
        }

        fetchRoutes()
    }

    private fun fetchRoutes() {
        RetrofitClient.instance.getRoutes().enqueue(object : Callback<ManageRoutesResponse> {
            override fun onResponse(
                call: Call<ManageRoutesResponse>,
                response: Response<ManageRoutesResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val routes = response.body()?.routes ?: emptyList()
                    routeList.clear()
                    routeList.addAll(routes)
                    routeAdapter.updateData(routeList)
                } else {
                    Toast.makeText(this@ManageRoutesActivity, "No routes found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ManageRoutesResponse>, t: Throwable) {
                Toast.makeText(this@ManageRoutesActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
