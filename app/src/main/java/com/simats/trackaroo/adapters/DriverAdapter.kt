package com.simats.trackaroo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.R
import com.simats.trackaroo.models.DriverData

class DriverAdapter(
    private var driverList: List<DriverData>,
    private val onItemClick: (DriverData) -> Unit   // ✅ Add click listener
) : RecyclerView.Adapter<DriverAdapter.DriverViewHolder>() {

    class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDriverID: TextView = itemView.findViewById(R.id.tvDriverID)
        val tvDriverName: TextView = itemView.findViewById(R.id.tvLicenseNo)
        val tvPhoneNo: TextView = itemView.findViewById(R.id.tvPhoneNo)
        val btnDeleteDriver: ImageView = itemView.findViewById(R.id.btnDeleteDriver)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver, parent, false)
        return DriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        val driver = driverList[position]

        holder.tvDriverID.text = driver.driver_id
        holder.tvDriverName.text = driver.driver_name
        holder.tvPhoneNo.text = driver.phone_number

        // ✅ Card click listener
        holder.itemView.setOnClickListener {
            onItemClick(driver)
        }

        // ❌ Delete button still not functional
        holder.btnDeleteDriver.setOnClickListener {
            // To be implemented later
        }
    }

    override fun getItemCount(): Int = driverList.size

    fun updateList(newList: List<DriverData>) {
        driverList = newList
        notifyDataSetChanged()
    }
}
