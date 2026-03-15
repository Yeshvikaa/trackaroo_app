package com.simats.trackaroo.models

data class DriverOtpResponse(
    val status: String,
    val message: String,
    val email: String? = null
)
