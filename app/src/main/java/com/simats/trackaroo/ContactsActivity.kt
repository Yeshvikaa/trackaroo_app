package com.simats.trackaroo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.trackaroo.models.ContactsData
import com.simats.trackaroo.models.ContactsResponse
import com.simats.trackaroo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactsActivity : AppCompatActivity() {

    private lateinit var adminNumber: TextView
    private lateinit var transportNumber: TextView
    private lateinit var policeNumber: TextView
    private lateinit var ambulanceNumber: TextView
    private lateinit var fireNumber: TextView

    private lateinit var callAdmin: ImageView
    private lateinit var callTransport: ImageView
    private lateinit var callPolice: ImageView
    private lateinit var callAmbulance: ImageView
    private lateinit var callFire: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contacts)

        // --- Bind views ---
        adminNumber = findViewById(R.id.adminnumber)
        transportNumber = findViewById(R.id.transportnumber)
        policeNumber = findViewById(R.id.policenumber)
        ambulanceNumber = findViewById(R.id.ambulancenumber)
        fireNumber = findViewById(R.id.firenumber)

        callAdmin = findViewById(R.id.calladminnumber)
        callTransport = findViewById(R.id.calltransportnumber)
        callPolice = findViewById(R.id.callpolice)
        callAmbulance = findViewById(R.id.callambulance)
        callFire = findViewById(R.id.callfire)

        findViewById<ImageView>(R.id.backButton).setOnClickListener { finish() }

        fetchContacts()
    }

    private fun fetchContacts() {
        val call = RetrofitClient.instance.getContacts()
        call.enqueue(object : Callback<ContactsResponse> {
            override fun onResponse(
                call: Call<ContactsResponse>,
                response: Response<ContactsResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val data = response.body()?.data
                    data?.let {
                        // Only update the Admin and Transport numbers from the server
                        adminNumber.text = it.admin
                        transportNumber.text = it.transport

                        // The emergency numbers (police, ambulance, fire) are hardcoded
                        // in the XML and should not be set here.

                        setupCallButtons()
                    }
                } else {
                    Toast.makeText(this@ContactsActivity, "No contacts found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ContactsResponse>, t: Throwable) {
                Toast.makeText(this@ContactsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupCallButtons() {
        // These will now correctly get the hardcoded numbers from the TextViews
        callAdmin.setOnClickListener { makeDial(adminNumber.text.toString()) }
        callTransport.setOnClickListener { makeDial(transportNumber.text.toString()) }
        callPolice.setOnClickListener { makeDial(policeNumber.text.toString()) }
        callAmbulance.setOnClickListener { makeDial(ambulanceNumber.text.toString()) }
        callFire.setOnClickListener { makeDial(fireNumber.text.toString()) }
    }

    private fun makeDial(number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }
}