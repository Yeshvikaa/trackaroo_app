package com.simats.trackaroo.models

data class DriverManagementResponse(
    val status: String,
    val data: List<DriverData>?
)

data class DriverData(
    val driver_id: String,
    val driver_name: String,
    val phone_number: String
)
