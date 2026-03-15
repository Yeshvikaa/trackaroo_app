package com.simats.trackaroo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.adapters.DriverContactsAdapter
import com.simats.trackaroo.models.DriverContact
import com.simats.trackaroo.models.DriverContactsResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverContactsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DriverContactsAdapter
    private lateinit var allContacts: List<DriverContact>  // keep full list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_contacts)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener { finish() }

        recyclerView = findViewById(R.id.driverRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // search bar
        val searchBar: EditText = findViewById(R.id.searchRoute)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }
        })

        fetchDriverContacts()
    }

    private fun fetchDriverContacts() {
        RetrofitClient.instance.getDriverContacts().enqueue(object : Callback<DriverContactsResponse> {
            override fun onResponse(
                call: Call<DriverContactsResponse>,
                response: Response<DriverContactsResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    allContacts = response.body()?.data ?: emptyList()
                    adapter = DriverContactsAdapter(allContacts) { number ->
                        makeCall(number)
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(
                        this@DriverContactsActivity,
                        response.body()?.message ?: "Error loading contacts",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<DriverContactsResponse>, t: Throwable) {
                Toast.makeText(
                    this@DriverContactsActivity,
                    "API Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterContacts(query: String) {
        val filtered = allContacts.filter {
            it.route_number.contains(query, ignoreCase = true)
        }
        adapter = DriverContactsAdapter(filtered) { number ->
            makeCall(number)
        }
        recyclerView.adapter = adapter
    }

    private fun makeCall(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot make call: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
