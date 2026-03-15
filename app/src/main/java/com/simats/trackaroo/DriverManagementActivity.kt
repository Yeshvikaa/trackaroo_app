package com.simats.trackaroo

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
import com.simats.trackaroo.models.DriverManagementResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverManagementActivity : AppCompatActivity() {

    private lateinit var driverAdapter: DriverAdapter
    private var driverList: MutableList<DriverData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_management)

        val backButton = findViewById<ImageView>(R.id.backButton)
        val searchBar = findViewById<EditText>(R.id.searchDriver)
        val recyclerView = findViewById<RecyclerView>(R.id.driversRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Initialize adapter with click listener
        driverAdapter = DriverAdapter(driverList) { driver ->
            Toast.makeText(this, "Clicked: ${driver.driver_name}", Toast.LENGTH_SHORT).show()
            // You can also navigate to another activity or open a dialog here
        }

        recyclerView.adapter = driverAdapter

        backButton.setOnClickListener { finish() }

        // 🔍 Search filtering
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDrivers(s.toString())
            }
        })

        fetchDrivers()
    }

    private fun fetchDrivers() {
        val call = RetrofitClient.instance.getDrivers()
        call.enqueue(object : Callback<DriverManagementResponse> {
            override fun onResponse(
                call: Call<DriverManagementResponse>,
                response: Response<DriverManagementResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    driverList.clear()
                    response.body()?.data?.let { driverList.addAll(it) }
                    driverAdapter.updateList(driverList)
                } else {
                    Toast.makeText(this@DriverManagementActivity, "No drivers found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DriverManagementResponse>, t: Throwable) {
                Toast.makeText(this@DriverManagementActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ✅ Filter drivers by name, ID, or phone
    private fun filterDrivers(query: String) {
        val filtered = driverList.filter {
            it.driver_name.contains(query, ignoreCase = true) ||
                    it.driver_id.contains(query, ignoreCase = true) ||
                    it.phone_number.contains(query)
        }
        driverAdapter.updateList(filtered)
    }

    // Optional: Delete driver locally
    private fun deleteDriver(driver: DriverData) {
        driverList.remove(driver)
        driverAdapter.updateList(driverList)
        Toast.makeText(this, "Deleted: ${driver.driver_name}", Toast.LENGTH_SHORT).show()

        // TODO: Call your backend API to delete driver permanently
        // e.g., RetrofitClient.instance.deleteDriver(driver.driver_id)
    }
}
