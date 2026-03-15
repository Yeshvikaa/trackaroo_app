package com.simats.trackaroo.models

data class AdminSignupRequest(
    val admin_id: String,
    val phone_number: String,
    val admin_email: String,
    val password: String,
    val confirm_password: String
)
