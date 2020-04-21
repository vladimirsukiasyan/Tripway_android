package com.tiparo.tripway.repository.network.api.services

import androidx.lifecycle.LiveData
import com.google.gson.annotations.SerializedName
import com.tiparo.tripway.repository.network.api.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsServices {

    @GET("maps/api/geocode/json")
    fun reverseGeocoding(@Query("latlng") location: String, @Query("key") apiKey: String): LiveData<ApiResponse<ReverseGeocodingResponse>>
}

data class ReverseGeocodingResponse(val results: List<GeocodingResult>){
    class GeocodingResult(@SerializedName("formatted_address") val address: String)
}