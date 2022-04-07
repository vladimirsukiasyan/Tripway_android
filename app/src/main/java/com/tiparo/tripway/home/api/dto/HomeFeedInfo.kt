package com.tiparo.tripway.home.api.dto

import com.google.gson.annotations.SerializedName
import com.tiparo.tripway.repository.network.api.services.TripsService

data class HomeFeedInfo(
    @SerializedName("anchor") val anchor: String?,
    @SerializedName("has_more") val hasMore: Boolean?,
    @SerializedName("points") val points: List<Point>
)