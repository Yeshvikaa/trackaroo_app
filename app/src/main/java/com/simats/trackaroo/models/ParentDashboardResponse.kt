// ParentDashboardResponse.kt
package com.simats.trackaroo.models

data class ParentDashboardResponse(
    val status: String,
    val data: ParentDashboardData?
)

data class ParentDashboardData(
    val student_name: String,
    val route_number: String,
    val route: String,
    val bus_number: String,
    val pickup_time: String,
    val eta_minutes_left: Int,
    val driver_name: String,
    val driver_phone: String
)
