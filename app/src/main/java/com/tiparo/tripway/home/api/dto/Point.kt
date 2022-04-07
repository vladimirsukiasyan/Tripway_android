package com.tiparo.tripway.home.api.dto

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class Point(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("photo") val photo: String,
    @SerializedName("updated") val updated: Timestamp,
    @SerializedName("username") val username: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("trip_id") val tripId: Long
)