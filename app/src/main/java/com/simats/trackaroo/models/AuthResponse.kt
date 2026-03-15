package com.simats.trackaroo.models

data class AuthResponse(
    val status: String,
    val message: String,
    val student_id: String? = null,
    val name: String? = null
)
