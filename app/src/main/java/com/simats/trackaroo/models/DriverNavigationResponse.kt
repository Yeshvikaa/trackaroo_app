package com.simats.trackaroo.models

import com.google.gson.annotations.SerializedName

data class DriverNavigationResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: Route?
)
