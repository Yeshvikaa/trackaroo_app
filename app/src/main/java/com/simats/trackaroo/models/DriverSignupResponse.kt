package com.simats.trackaroo.models
data class DriverSignupRequest(
    val driver_id: String,
    val driver_name: String,
    val phone_number: String,
    val license_number: String,
    val password: String,
    val confirm_password: String
)
