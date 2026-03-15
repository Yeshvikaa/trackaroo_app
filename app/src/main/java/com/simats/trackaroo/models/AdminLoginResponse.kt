package com.simats.trackaroo.models

data class AdminLoginResponse(
    val status: String,
    val message: String,
    val admin_id: String? = null
)
