package com.simats.trackaroo.models

data class AdminSignupResponse(
    val status: String,
    val message: String,
    val admin_id: String? = null,
    val email: String? = null
)
