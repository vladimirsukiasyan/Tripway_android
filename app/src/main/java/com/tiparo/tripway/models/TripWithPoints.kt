package com.tiparo.tripway.models

import android.net.Uri
import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse.GeocodingResult.AddressComponent

@Entity
data class TripWithPoints(
    @Embedded val trip: Trip,
    @Relation(
        parentColumn = "id",
        entityColumn = "trip_id"
    )
    val points: List<Point>
)