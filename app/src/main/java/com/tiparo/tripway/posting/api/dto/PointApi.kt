package com.tiparo.tripway.posting.api.dto

import com.google.gson.annotations.SerializedName

data class PointApi(
    @SerializedName("name") val name: String,
    @SerializedName("location") val location: Location,
    @SerializedName("description") val description: String,
    @SerializedName("trip_id") val tripId: Long?,
    @SerializedName("trip_name") val tripName: String?
) {
    data class Location(
        @SerializedName("lat") val lat: Double,
        @SerializedName("lng") val lng: Double,
        @SerializedName("address") val address: String,
        @SerializedName("address_components") val addressComponents: String
    )
}