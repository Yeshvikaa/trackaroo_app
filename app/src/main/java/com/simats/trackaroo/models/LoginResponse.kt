package com.simats.trackaroo.models
data class LoginResponse(
    val status: String,
    val message: String,
    val student_id: String? = null
)
