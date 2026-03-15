package com.simats.trackaroo.models

data class AddRouteRequest(
    val route_number: String,
    val route: String,
    val time: String,
    val bus_number: String
)
