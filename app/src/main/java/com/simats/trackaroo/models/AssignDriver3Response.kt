package com.simats.trackaroo.models

data class AssignDriver3Response(
    val status: String,
    val message: String,
    val data: DriverRouteData? = null
)

data class DriverRouteData(
    val driver_id: String,
    val route_number: String,
    val route: String,
    val time: String,
    val bus_number: String
)
