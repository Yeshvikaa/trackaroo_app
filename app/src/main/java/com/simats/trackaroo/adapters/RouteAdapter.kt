package com.simats.trackaroo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.trackaroo.R
import com.simats.trackaroo.models.Route

class RouteAdapter(
    private var routes: List<Route>,
    private val onEdit: (Route) -> Unit,
    private val onDelete: (Route) -> Unit
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    class RouteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val routeTitle: TextView = view.findViewById(R.id.routeTitle)
        val routeDetails: TextView = view.findViewById(R.id.routeDetails)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val editIcon: ImageView = view.findViewById(R.id.editIcon)
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.routeTitle.text = "Route ${route.route_number}  Bus: ${route.bus_number}"
        holder.routeDetails.text = route.route
        holder.timeText.text = route.time

        holder.editIcon.setOnClickListener { onEdit(route) }
        holder.deleteIcon.setOnClickListener { onDelete(route) }
    }

    override fun getItemCount(): Int = routes.size

    fun updateData(newRoutes: List<Route>) {
        routes = newRoutes
        notifyDataSetChanged()
    }
}
