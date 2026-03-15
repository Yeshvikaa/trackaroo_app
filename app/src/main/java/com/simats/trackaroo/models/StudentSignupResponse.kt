package com.simats.trackaroo.models

data class StudentSignupResponse(
    val status: String,
    val message: String,
    val student_id: String? = null
)
