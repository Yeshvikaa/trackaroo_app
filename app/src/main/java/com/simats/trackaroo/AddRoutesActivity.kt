package com.simats.trackaroo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.AddRouteRequest
import com.simats.trackaroo.models.AddRouteResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRoutesActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var routeNumberEdit: EditText
    private lateinit var routeEdit: EditText
    private lateinit var timeEdit: EditText
    private lateinit var busNumberEdit: EditText
    private lateinit var confirmBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_routes)

        // Initialize views
        backButton = findViewById(R.id.backButton)
        routeNumberEdit = findViewById(R.id.routeNumberEdit)
        routeEdit = findViewById(R.id.routeEdit)
        timeEdit = findViewById(R.id.timeEdit)
        busNumberEdit = findViewById(R.id.busNumberEdit)
        confirmBtn = findViewById(R.id.confirmroutebtn)

        backButton.setOnClickListener { finish() }

        confirmBtn.setOnClickListener {
            val routeNumber = routeNumberEdit.text.toString().trim()
            val route = routeEdit.text.toString().trim()
            val time = timeEdit.text.toString().trim()
            val busNumber = busNumberEdit.text.toString().trim()

            if (routeNumber.isEmpty() || route.isEmpty() || time.isEmpty() || busNumber.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addRoute(routeNumber, route, time, busNumber)
        }
    }

    private fun addRoute(routeNumber: String, route: String, time: String, busNumber: String) {
        val request = AddRouteRequest(
            route_number = routeNumber,
            route = route,
            time = time,
            bus_number = busNumber
        )

        RetrofitClient.instance.addRoute(request)
            .enqueue(object : Callback<AddRouteResponse> {
                override fun onResponse(
                    call: Call<AddRouteResponse>,
                    response: Response<AddRouteResponse>
                ) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        Toast.makeText(
                            this@AddRoutesActivity,
                            res?.message ?: "Route added successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        if (res?.status == "success") {
                            finish() // Close screen after adding route
                        }
                    } else {
                        Toast.makeText(
                            this@AddRoutesActivity,
                            "Server error: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AddRouteResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddRoutesActivity,
                        "Failed: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
