package com.simats.trackaroo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.R
import com.simats.trackaroo.models.DriverData

class AssignDriver2Adapter(
    private var drivers: List<DriverData>,
    private val onDriverClick: (DriverData) -> Unit,
    private val onDeleteClick: (DriverData) -> Unit
) : RecyclerView.Adapter<AssignDriver2Adapter.DriverViewHolder>() {

    class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDriverID: TextView = itemView.findViewById(R.id.tvDriverID)
        val tvLicenseNo: TextView = itemView.findViewById(R.id.tvLicenseNo)
        val tvPhoneNo: TextView = itemView.findViewById(R.id.tvPhoneNo)
        val btnDeleteDriver: ImageView = itemView.findViewById(R.id.btnDeleteDriver)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver, parent, false)
        return DriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        val driver = drivers[position]

        holder.tvDriverID.text = "Driver ID: ${driver.driver_id}"
        holder.tvLicenseNo.text = "Driver name: ${driver.driver_name}"
        holder.tvPhoneNo.text = "Phone: ${driver.phone_number}"

        // ✅ Card click → open AssignDriver3
        holder.itemView.setOnClickListener {
            onDriverClick(driver)
        }

        // ✅ Delete button click
        holder.btnDeleteDriver.setOnClickListener {
            onDeleteClick(driver)
        }
    }

    override fun getItemCount(): Int = drivers.size

    fun updateData(newDrivers: List<DriverData>) {
        drivers = newDrivers
        notifyDataSetChanged()
    }
}
