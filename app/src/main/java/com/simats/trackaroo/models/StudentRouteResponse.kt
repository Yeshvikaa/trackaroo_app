package com.simats.trackaroo.models

import com.google.gson.annotations.SerializedName

data class StudentRouteResponse(
    @SerializedName("status")
    val status: String?,

    @SerializedName("data")
    val data: Data?
) {
    data class Data(
        @SerializedName("student_name")
        val student_name: String?,

        @SerializedName("date")
        val date: String?,

        @SerializedName("bus_number")
        val bus_number: String?,

        @SerializedName("eta_minutes_left")
        val eta_minutes_left: Int?, // Changed to Int since API returns a number

        @SerializedName("pickup_time")
        val pickup_time: String?,

        @SerializedName("route")
        val route: String?,

        @SerializedName("latitude")
        val latitude: Double?,  // Added

        @SerializedName("longitude")
        val longitude: Double?  // Added
    )
}
