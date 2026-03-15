package com.simats.trackaroo.models

data class StudentAccountSetupRequest(
    val student_id: String,
    val student_email: String,
    val password: String,
    val confirm_password: String
)
