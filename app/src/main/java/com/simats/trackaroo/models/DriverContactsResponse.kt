package com.simats.trackaroo.models

data class DriverContactsResponse(
    val status: String,
    val data: List<DriverContact>?,
    val message: String?
)
