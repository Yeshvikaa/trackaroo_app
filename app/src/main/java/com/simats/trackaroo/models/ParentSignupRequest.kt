package com.simats.trackaroo.models

data class ParentSignupRequest(
    val student_id: String,
    val student_name: String,   // match PHP
    val parent_email: String,   // match PHP
    val phone_number: String,   // match PHP
    val password: String,
    val confirm_password: String
)
