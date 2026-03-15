package com.simats.trackaroo.models

data class ContactsResponse(
    val status: String,
    val message: String? = null,
    val data: ContactsData? = null
)

data class ContactsData(
    val admin: String,
    val transport: String,
    val police: String,
    val ambulance: String,
    val fire_service: String
)
