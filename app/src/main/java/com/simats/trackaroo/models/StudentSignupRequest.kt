package com.simats.trackaroo.models

data class StudentSignupRequest(
    val student_id: String,
    val name: String,
    val age: Int,
    val grade: String,
    val school: String,
    val address: String
)
