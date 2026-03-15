package com.simats.trackaroo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.AdminContactsRequest
import com.simats.trackaroo.models.AdminContactsResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminContactsActivity : AppCompatActivity() {

    private lateinit var transportNumber: EditText
    private lateinit var adminNumber: EditText
    private lateinit var addButton: Button
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_contacts)

        // Bind views
        transportNumber = findViewById(R.id.transportnumber)
        adminNumber = findViewById(R.id.adminnumber)
        addButton = findViewById(R.id.addbutton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener { finish() }

        addButton.setOnClickListener {
            val transport = transportNumber.text.toString().trim()
            val admin = adminNumber.text.toString().trim()

            if (transport.isEmpty() || admin.isEmpty()) {
                Toast.makeText(this, "Please enter both numbers", Toast.LENGTH_SHORT).show()
            } else {
                addContactsToServer(transport, admin)
            }
        }
    }

    private fun addContactsToServer(transport: String, admin: String) {
        val request = AdminContactsRequest(transport, admin)

        RetrofitClient.instance.addAdminContacts(request)
            .enqueue(object : Callback<AdminContactsResponse> {
                override fun onResponse(
                    call: Call<AdminContactsResponse>,
                    response: Response<AdminContactsResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val res = response.body()!!
                        Toast.makeText(this@AdminContactsActivity, res.message, Toast.LENGTH_SHORT).show()

                        if (res.status == "success") {
                            transportNumber.text.clear()
                            adminNumber.text.clear()
                        }
                    } else {
                        Toast.makeText(this@AdminContactsActivity, "Server error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AdminContactsResponse>, t: Throwable) {
                    Toast.makeText(this@AdminContactsActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
