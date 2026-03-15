package com.simats.trackaroo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.R
import com.simats.trackaroo.models.Route

class DriverRouteAdapter(private val routeList: List<Route>) :
    RecyclerView.Adapter<DriverRouteAdapter.RouteViewHolder>() {

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRouteNo: TextView = itemView.findViewById(R.id.tvRouteNo)
        val tvRoute: TextView = itemView.findViewById(R.id.tvRoute)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.route_item, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routeList[position]
        holder.tvRouteNo.text = route.route_number
        holder.tvRoute.text = route.route
        holder.tvTime.text = route.time
    }

    override fun getItemCount(): Int = routeList.size
}
