package com.simats.trackaroo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.R
import com.simats.trackaroo.models.Route

class AssignDriver1Adapter(
    private var routes: List<Route>,
    private val onItemClick: (Route) -> Unit   // <-- Added click listener
) : RecyclerView.Adapter<AssignDriver1Adapter.RouteViewHolder>() {

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routeTitle: TextView = itemView.findViewById(R.id.routeTitle)
        val routeDetails: TextView = itemView.findViewById(R.id.routeDetails)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.assign_route_1, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.routeTitle.text = "Route ${route.route_number}   Bus no: ${route.bus_number}"
        holder.routeDetails.text = route.route
        holder.timeText.text = route.time

        // ✅ Handle item click
        holder.itemView.setOnClickListener {
            onItemClick(route)
        }
    }

    override fun getItemCount(): Int = routes.size
}
