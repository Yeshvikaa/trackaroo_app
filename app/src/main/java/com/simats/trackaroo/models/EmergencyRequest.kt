package com.simats.trackaroo.models

data class EmergencyRequest(
    val user_type: String,
    val user_id: String,
    val message: String,
    val route_number: String
)
