package com.simats.trackaroo.models

data class AdminDashboardResponse(
    val status: String,
    val data: AdminDashboardData
)

data class AdminDashboardData(
    val buses: Int,
    val students: Int,
    val routes: Int,
    val drivers: Int
)
