package com.simats.trackaroo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.R
import com.simats.trackaroo.models.DriverContact

class DriverContactsAdapter(
    private val contacts: List<DriverContact>,
    private val onCallClick: (String) -> Unit
) : RecyclerView.Adapter<DriverContactsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRouteNumber: TextView = itemView.findViewById(R.id.tvRouteNumber)
        val tvDriverNumber: TextView = itemView.findViewById(R.id.tvDriverNumber)
        val btnCallDriver: ImageView = itemView.findViewById(R.id.btnCallDriver)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.driver_contact_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.tvRouteNumber.text = "${contact.route_number}"
        holder.tvDriverNumber.text = contact.phone_number

        holder.btnCallDriver.setOnClickListener {
            onCallClick(contact.phone_number)
        }
    }

    override fun getItemCount(): Int = contacts.size
}
