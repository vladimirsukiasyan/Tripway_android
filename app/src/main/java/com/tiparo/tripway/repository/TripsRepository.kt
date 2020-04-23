package com.tiparo.tripway.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.maps.model.LatLng
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.BuildConfig
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.repository.network.api.*
import com.tiparo.tripway.repository.network.api.services.GoogleMapsServices
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse
import com.tiparo.tripway.repository.network.api.services.TripsService
import timber.log.Timber
import javax.inject.Inject

class TripsRepository @Inject constructor(
    private val appExecutors: AppExecutors,
//    private val tripDao: TripDao,
    private val tripsService: TripsService,
    private val googleMapsService: GoogleMapsServices
//    private val db: TripwayDb
) {

    private val tripsMock = MutableList(10) { id ->
        TripsService.Trip(
            id = id.toString(),
            trip_name = "Trip $id",
            is_completed = id % 2 == 0,
            first_point_name = "Tokyo",
            last_point_name = "Baikal",
            user_id = "id$id"
        )
    }

    fun loadMyTrips(): LiveData<Resource<List<TripsService.Trip>>> {
        return object :
            NetworkBoundResource<List<TripsService.Trip>, List<TripsService.Trip>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<List<TripsService.Trip>>> {
                return tripsService.getOwnTrips()
            }
        }.asLiveData()
    }

    fun reverseGeocode(location: LatLng): LiveData<Resource<String>> {
        return object : NetworkBoundResource<String, ReverseGeocodingResponse>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ReverseGeocodingResponse>> {
                return googleMapsService.reverseGeocoding(
                    convertLatLng(location),
                    BuildConfig.GOOGLE_MAPS_KEY
                )
            }

            override fun mapDTO(response: ApiSuccessResponse<ReverseGeocodingResponse>): ApiSuccessResponse<String> {
                return ApiSuccessResponse(
                    response.body.results.firstOrNull()?.address
                        ?: "No information from Google. Try another"
                )
            }
        }.asLiveData()
    }

    fun convertLatLng(location: LatLng) = "${location.latitude},${location.longitude}"

    fun loadMyTripsMock(): LiveData<Resource<List<TripsService.Trip>>> {
        return MutableLiveData(Resource.success(tripsMock))
    }
}