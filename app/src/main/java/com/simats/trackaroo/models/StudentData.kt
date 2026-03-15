package com.simats.trackaroo.models

data class StudentDatabaseResponse(
    val status: String,
    val data: List<StudentData>?
)

data class StudentData(
    val student_id: String,
    val student_name: String,
    val grade: String,
    val route_number: String? // ✅ Nullable to handle NULL in DB
)
